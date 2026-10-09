package com.maboy.applicantmarket.assignment.controller;

import com.maboy.applicantmarket.assignment.model.VacancyAssignment;
import com.maboy.applicantmarket.assignment.model.request.CreateAssignmentRequest;
import com.maboy.applicantmarket.assignment.model.request.UpdateAssignmentRequest;
import com.maboy.applicantmarket.assignment.service.AssignmentService;
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
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Assignment", description = "Тестовые задания по вакансии")
@RestController
@RequestMapping("/api/assignment")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @Operation(summary = "Создать тестовое задание")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задание создано"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или у вакансии уже есть задание",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем или вакансия принадлежит другой компании",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Вакансия не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PostMapping("/vacancy/{vacancyId}")
    public VacancyAssignment create(@PathVariable UUID vacancyId,
                                    @Valid @RequestBody CreateAssignmentRequest request,
                                    @Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return assignmentService.create(ownerId, vacancyId, request);
    }

    @Operation(summary = "Получить задание по вакансии",
            description = "PUBLISHED доступна всем, DRAFT и CLOSED — только владельцу.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задание найдено"),
            @ApiResponse(responseCode = "404", description = "Вакансия или задание не найдены",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/vacancy/{vacancyId}")
    public VacancyAssignment getByVacancy(@PathVariable UUID vacancyId,
                                          @Parameter(hidden = true) @CurrentUser UUID requesterId) {
        return assignmentService.getByVacancy(requesterId, vacancyId);
    }

    @Operation(summary = "Обновить задание")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задание обновлено"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем или вакансия принадлежит другой компании",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Задание не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PatchMapping("/vacancy/{vacancyId}")
    public VacancyAssignment update(@PathVariable UUID vacancyId,
                                    @Valid @RequestBody UpdateAssignmentRequest request,
                                    @Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return assignmentService.update(ownerId, vacancyId, request);
    }

    @Operation(summary = "Удалить задание")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задание удалено"),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем или вакансия принадлежит другой компании",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Задание не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @DeleteMapping("/vacancy/{vacancyId}")
    public void delete(@PathVariable UUID vacancyId,
                       @Parameter(hidden = true) @CurrentUser UUID ownerId) {
        assignmentService.delete(ownerId, vacancyId);
    }
}