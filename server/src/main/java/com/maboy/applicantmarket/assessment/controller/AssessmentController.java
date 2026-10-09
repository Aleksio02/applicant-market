package com.maboy.applicantmarket.assessment.controller;

import com.maboy.applicantmarket.assessment.converter.AssessmentItemConverter;
import com.maboy.applicantmarket.assessment.converter.AssessmentSessionConverter;
import com.maboy.applicantmarket.assessment.dao.AssessmentAnswerDao;
import com.maboy.applicantmarket.assessment.model.AssessmentItem;
import com.maboy.applicantmarket.assessment.model.AssessmentSession;
import com.maboy.applicantmarket.assessment.model.request.StartAssessmentRequest;
import com.maboy.applicantmarket.assessment.model.request.SubmitAnswerRequest;
import com.maboy.applicantmarket.assessment.model.response.*;
import com.maboy.applicantmarket.assessment.service.AssessmentSessionService;
import com.maboy.applicantmarket.assessment.service.grading.GradingOutcome;
import com.maboy.applicantmarket.auth.config.annotation.CurrentUser;
import com.maboy.applicantmarket.commons.model.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Assessment", description = "Тестирование на грейд: сессии, ответы, оценка")
@RestController
@RequestMapping("/api/assessment")
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentSessionService sessionService;
    private final AssessmentSessionConverter sessionConverter;
    private final AssessmentItemConverter itemConverter;
    private final AssessmentAnswerDao answerDao;

    @Operation(
            summary = "Начать сессию тестирования",
            description = """
                    Создаёт сессию тестирования по указанному навыку и заявленному грейду.
                    На основе claimed-грейда подбираются задания нужной сложности.
                    Возвращает список сгенерированных заданий (без правильных ответов).
                    Один пользователь может иметь не более одной активной сессии на навык.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сессия создана",
                    content = @Content(schema = @Schema(implementation = AssessmentSessionResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является соискателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Профиль соискателя или грейд не найдены",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Активная сессия уже существует или не истёк cooldown",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PostMapping("/sessions")
    public AssessmentSessionResponse startSession(
            @Parameter(hidden = true) @CurrentUser UUID userId,
            @RequestBody StartAssessmentRequest request
    ) {
        AssessmentSession session = sessionService.startSession(
                userId, request.getSkillId(), request.getClaimedGradeId());

        List<AssessmentItemResponse> items = buildItemResponses(session.getId());
        return sessionConverter.toResponse(session, items);
    }

    @Operation(
            summary = "Получить сессию",
            description = "Возвращает сессию и её задания. Правильные ответы не возвращаются никогда."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сессия найдена",
                    content = @Content(schema = @Schema(implementation = AssessmentSessionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Сессия не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "410", description = "Сессия истекла",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @GetMapping("/sessions/{id}")
    public AssessmentSessionResponse getSession(
            @Parameter(hidden = true) @CurrentUser UUID userId,
            @PathVariable UUID id
    ) {
        AssessmentSession session = sessionService.getSession(userId, id);
        List<AssessmentItemResponse> items = buildItemResponses(session.getId());
        return sessionConverter.toResponse(session, items);
    }

    @Operation(
            summary = "Ответить на задание",
            description = "Принимает ответ на одно задание. Повторный ответ на то же задание запрещён."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ответ принят"),
            @ApiResponse(responseCode = "404", description = "Сессия или задание не найдены",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Ответ уже существует или сессия неактивна",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "410", description = "Сессия истекла",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PostMapping("/sessions/{id}/answers")
    public void submitAnswer(
            @Parameter(hidden = true) @CurrentUser UUID userId,
            @PathVariable UUID id,
            @RequestBody SubmitAnswerRequest request
    ) {
        sessionService.submitAnswer(userId, id, request.getItemId(), request.getAnswer());
    }

    @Operation(
            summary = "Завершить сессию",
            description = """
                    Завершает сессию, подсчитывает результат и присваивает грейд.
                    Если score < pass — сессия FAILED, грейд не присваивается.
                    Если score >= promote — грейд повышается на следующий уровень.
                    Иначе грейд остаётся тем же.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сессия завершена",
                    content = @Content(schema = @Schema(implementation = AssessmentResultResponse.class))),
            @ApiResponse(responseCode = "404", description = "Сессия не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Не все задания отвечены или сессия уже завершена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "410", description = "Сессия истекла",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PostMapping("/sessions/{id}/complete")
    public AssessmentResultResponse completeSession(
            @Parameter(hidden = true) @CurrentUser UUID userId,
            @PathVariable UUID id
    ) {
        GradingOutcome outcome = sessionService.completeSession(userId, id);
        return AssessmentResultResponse.builder()
                .sessionId(id)
                .status(outcome.status())
                .score(outcome.score())
                .resultGradeId(outcome.resultGradeId())
                .build();
    }

    @Operation(
            summary = "История тестирований",
            description = "Возвращает все сессии текущего пользователя, отсортированные по дате старта (сначала новые)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список получен")
    })
    @SecurityRequirement(name = "sessionCookie")
    @GetMapping("/sessions/history")
    public List<AssessmentHistoryItemResponse> history(
            @Parameter(hidden = true) @CurrentUser UUID userId
    ) {
        List<AssessmentHistoryItemResponse> result = sessionService.getHistory(userId).stream()
                .map(s -> AssessmentHistoryItemResponse.builder()
                        .sessionId(s.getId())
                        .skillId(s.getSkillId())
                        .claimedGradeId(s.getClaimedGradeId())
                        .resultGradeId(s.getResultGradeId())
                        .status(s.getStatus())
                        .score(s.getScore())
                        .startedAt(s.getStartedAt())
                        .completedAt(s.getCompletedAt())
                        .build())
                .toList();
        return result;
    }

    private List<AssessmentItemResponse> buildItemResponses(UUID sessionId) {
        List<AssessmentItem> items = sessionService.loadItems(sessionId);
        if (items.isEmpty()) {
            return List.of();
        }

        List<UUID> itemIds = items.stream().map(AssessmentItem::getId).toList();
        Set<UUID> answeredIds = answerDao.findAllByItemIdIn(itemIds).stream()
                .map(a -> a.getItemId())
                .collect(Collectors.toSet());

        List<AssessmentItemResponse> result = new ArrayList<>(items.size());
        for (AssessmentItem item : items) {
            result.add(itemConverter.toResponse(item, answeredIds.contains(item.getId())));
        }
        return result;
    }
}