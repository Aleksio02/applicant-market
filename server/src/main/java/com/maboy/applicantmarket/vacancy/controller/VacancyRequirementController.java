package com.maboy.applicantmarket.vacancy.controller;

import com.maboy.applicantmarket.auth.config.annotation.CurrentUser;
import com.maboy.applicantmarket.commons.model.response.ErrorResponse;
import com.maboy.applicantmarket.vacancy.model.VacancyRequirement;
import com.maboy.applicantmarket.vacancy.model.request.CreateVacancyRequirementRequest;
import com.maboy.applicantmarket.vacancy.model.request.UpdateVacancyRequirementRequest;
import com.maboy.applicantmarket.vacancy.service.VacancyRequirementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Vacancy / Requirement", description = "Требования к вакансии")
@RestController
@RequestMapping("/api/vacancy/requirement")
public class VacancyRequirementController {

    private final VacancyRequirementService requirementService;

    public VacancyRequirementController(VacancyRequirementService requirementService) {
        this.requirementService = requirementService;
    }

    @Operation(summary = "Создать требование")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Требование создано"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или навык уже добавлен",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Вакансия или навык не найдены",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PostMapping("/vacancy/{vacancyId}")
    public VacancyRequirement create(@PathVariable UUID vacancyId,
                                     @Valid @RequestBody CreateVacancyRequirementRequest request,
                                     @Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return requirementService.create(ownerId, vacancyId, request);
    }

    @Operation(summary = "Список требований вакансии",
            description = "PUBLISHED доступна всем, DRAFT и CLOSED — только владельцу.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список получен"),
            @ApiResponse(responseCode = "404", description = "Вакансия не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/vacancy/{vacancyId}")
    public List<VacancyRequirement> getList(@PathVariable UUID vacancyId,
                                            @Parameter(hidden = true) @CurrentUser UUID requesterId) {
        return requirementService.getList(requesterId, vacancyId);
    }

    @Operation(summary = "Обновить требование")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Требование обновлено"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Вакансия или требование не найдены",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PatchMapping("/{requirementId}/vacancy/{vacancyId}")
    public VacancyRequirement update(@PathVariable UUID vacancyId,
                                     @PathVariable UUID requirementId,
                                     @Valid @RequestBody UpdateVacancyRequirementRequest request,
                                     @Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return requirementService.update(ownerId, vacancyId, requirementId, request);
    }

    @Operation(summary = "Удалить требование")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Требование удалено"),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Вакансия или требование не найдены",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @DeleteMapping("/{requirementId}/vacancy/{vacancyId}")
    public void delete(@PathVariable UUID vacancyId,
                       @PathVariable UUID requirementId,
                       @Parameter(hidden = true) @CurrentUser UUID ownerId) {
        requirementService.delete(ownerId, vacancyId, requirementId);
    }
}