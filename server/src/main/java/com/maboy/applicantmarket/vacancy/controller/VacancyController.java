package com.maboy.applicantmarket.vacancy.controller;

import com.maboy.applicantmarket.auth.config.annotation.CurrentUser;
import com.maboy.applicantmarket.commons.model.response.ErrorResponse;
import com.maboy.applicantmarket.commons.model.response.PageResponse;
import com.maboy.applicantmarket.vacancy.model.Vacancy;
import com.maboy.applicantmarket.vacancy.model.request.CreateVacancyRequest;
import com.maboy.applicantmarket.vacancy.model.request.GetVacancyListRequest;
import com.maboy.applicantmarket.vacancy.model.request.UpdateVacancyRequest;
import com.maboy.applicantmarket.vacancy.service.VacancyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Vacancy", description = "Публикация и управление вакансиями")
@RestController
@RequestMapping("/api/vacancy")
public class VacancyController {

    private final VacancyService vacancyService;

    public VacancyController(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    @Operation(summary = "Создать вакансию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Вакансия создана в статусе DRAFT"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или salaryFrom > salaryTo",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Компания или справочные данные не найдены",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PostMapping
    public Vacancy create(@Valid @RequestBody CreateVacancyRequest request,
                          @Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return vacancyService.create(ownerId, request);
    }

    @Operation(summary = "Список опубликованных вакансий")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список получен")
    })
    @GetMapping
    public PageResponse<Vacancy> getPublished(GetVacancyListRequest request) {
        return vacancyService.getListForApplicant(request);
    }

    @Operation(summary = "Мои вакансии")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список получен"),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Компания не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @GetMapping("/mine")
    public PageResponse<Vacancy> getMine(GetVacancyListRequest request,
                                         @Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return vacancyService.getListForEmployer(ownerId, request);
    }

    @Operation(summary = "Одна вакансия",
            description = "PUBLISHED доступна всем, DRAFT и CLOSED — только владельцу.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Вакансия найдена"),
            @ApiResponse(responseCode = "403", description = "Вакансия не опубликована и пользователь не её владелец",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Вакансия не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public Vacancy getById(@PathVariable UUID id,
                           @Parameter(hidden = true) @CurrentUser UUID requesterId) {
        return vacancyService.getById(requesterId, id);
    }

    @Operation(summary = "Обновить вакансию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Вакансия обновлена"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Вакансия не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PatchMapping("/{id}")
    public Vacancy update(@PathVariable UUID id,
                          @Valid @RequestBody UpdateVacancyRequest request,
                          @Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return vacancyService.update(ownerId, id, request);
    }

    @Operation(summary = "Опубликовать вакансию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Вакансия опубликована"),
            @ApiResponse(responseCode = "400", description = "Вакансия уже опубликована или закрыта",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Вакансия не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PatchMapping("/{id}/publish")
    public Vacancy publish(@PathVariable UUID id,
                           @Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return vacancyService.publish(ownerId, id);
    }

    @Operation(summary = "Закрыть вакансию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Вакансия закрыта"),
            @ApiResponse(responseCode = "400", description = "Вакансия уже закрыта",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Вакансия не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PatchMapping("/{id}/close")
    public Vacancy close(@PathVariable UUID id,
                         @Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return vacancyService.close(ownerId, id);
    }
}