package com.maboy.applicantmarket.matching.controller;

import com.maboy.applicantmarket.auth.config.annotation.CurrentUser;
import com.maboy.applicantmarket.matching.model.request.CandidateSearchRequest;
import com.maboy.applicantmarket.matching.model.response.CandidateCardResponse;
import com.maboy.applicantmarket.matching.model.response.CandidateSearchResponse;
import com.maboy.applicantmarket.matching.model.response.CategorySummaryResponse;
import com.maboy.applicantmarket.matching.service.MatchingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
@Tag(name = "Matching", description = "Подбор кандидатов для работодателя")
@SecurityRequirement(name = "sessionCookie")
public class MatchingController {

    private final MatchingService matchingService;

    // ============================================================
    // POST /search
    // ============================================================
    @PostMapping("/search")
    @Operation(
        summary = "Поиск кандидатов по описанию потребности",
        description = """
            Возвращает список кандидатов, соответствующих указанной специализации и грейду.
            Требования по стеку (skillIds) опциональны: если они заданы, каждый кандидат получает
            метрику matchLevel (0..1) — долю совпавших навыков, взвешенную с соответствием грейда.
            
            Каждый кандидат в выдаче содержит rankScore (общая оценка релевантности) и
            список reasons — объяснение, почему он попал в выдачу и на какую позицию.
            
            Результат сохраняется в истории query — его можно воспроизвести через
            GET /queries/{queryId}.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Выдача сформирована",
            content = @Content(schema = @Schema(implementation = CandidateSearchResponse.class))),
        @ApiResponse(responseCode = "400", description = "Некорректный запрос (отсутствует specializationId/gradeId, неизвестный sortBy, minMatchLevel вне [0,1])"),
        @ApiResponse(responseCode = "401", description = "Не аутентифицирован"),
        @ApiResponse(responseCode = "403", description = "Не работодатель")
    })
    public CandidateSearchResponse search(
        @Parameter(hidden = true)
        @CurrentUser UUID employerId,
        @RequestBody CandidateSearchRequest request
    ) {
        return matchingService.search(employerId, request);
    }

    // ============================================================
    // GET /categories
    // ============================================================
    @GetMapping("/categories")
    @Operation(
        summary = "Сводка по категориям",
        description = """
            Возвращает список всех активных категорий (специализация + грейд) с количеством
            кандидатов в каждой. Используется для навигации: работодатель видит, где вообще
            есть люди, прежде чем запускать поиск.
            
            Счётчик включает всех кандидатов в категории, без учёта настройки приватности
            конкретных пользователей.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список категорий",
            content = @Content(schema = @Schema(implementation = CategorySummaryResponse.class))),
        @ApiResponse(responseCode = "401", description = "Не аутентифицирован"),
        @ApiResponse(responseCode = "403", description = "Не работодатель")
    })
    public List<CategorySummaryResponse> categories(
        @Parameter(hidden = true)
        @CurrentUser UUID employerId
    ) {
        return matchingService.getCategorySummary();
    }

    // ============================================================
    // GET /candidates/{applicantId}
    // ============================================================
    @GetMapping("/candidates/{applicantId}")
    @Operation(
        summary = "Карточка кандидата",
        description = """
            Возвращает детали конкретного кандидата без контактных данных: имя, город,
            primary-навык и грейд, категорию, счётчики навыков, объяснение релевантности.
            
            Контактные данные (email, телефон) не возвращаются никогда — их раскрывает только
            модуль interaction после того, как кандидат принял приглашение.
            
            Если кандидат отключил видимость в поиске (visible_in_search = false) или
            не категоризован (нет verified grade по primary-навыку), возвращается 404.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Карточка кандидата",
            content = @Content(schema = @Schema(implementation = CandidateCardResponse.class))),
        @ApiResponse(responseCode = "401", description = "Не аутентифицирован"),
        @ApiResponse(responseCode = "403", description = "Не работодатель"),
        @ApiResponse(responseCode = "404", description = "Кандидат не найден, скрыт или не категоризован")
    })
    public CandidateCardResponse candidate(
        @Parameter(hidden = true)
        @CurrentUser UUID employerId,
        @Parameter(description = "ID профиля соискателя", required = true)
        @PathVariable UUID applicantId
    ) {
        return matchingService.getCandidateCard(applicantId);
    }

    // ============================================================
    // GET /queries/{queryId}
    // ============================================================
    @GetMapping("/queries/{queryId}")
    @Operation(
        summary = "Повторный просмотр выдачи",
        description = """
            Восстанавливает ранее сформированную выдачу по её queryId. Используется для
            аудита и объяснимости: работодатель может вернуться к прошлому поиску и
            увидеть те же результаты с теми же объяснениями.
            
            Возвращает только те queries, которые принадлежат текущему работодателю.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Выдача восстановлена",
            content = @Content(schema = @Schema(implementation = CandidateSearchResponse.class))),
        @ApiResponse(responseCode = "401", description = "Не аутентифицирован"),
        @ApiResponse(responseCode = "403", description = "Не работодатель"),
        @ApiResponse(responseCode = "404", description = "Query не найден или принадлежит другому работодателю")
    })
    public ResponseEntity<CandidateSearchResponse> replayQuery(
        @Parameter(hidden = true)
        @CurrentUser UUID employerId,
        @Parameter(description = "ID сохранённого поискового запроса", required = true)
        @PathVariable UUID queryId
    ) {
        return ResponseEntity.ok(matchingService.replayQuery(employerId, queryId));
    }
}