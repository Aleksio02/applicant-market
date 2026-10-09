package com.maboy.applicantmarket.assignment.controller;

import com.maboy.applicantmarket.assignment.model.AssignmentAttempt;
import com.maboy.applicantmarket.assignment.model.request.EvaluateAttemptRequest;
import com.maboy.applicantmarket.assignment.model.request.SubmitAttemptRequest;
import com.maboy.applicantmarket.assignment.service.AssignmentAttemptService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Assignment / Attempt", description = "Попытки прохождения тестовых заданий")
@RestController
@RequestMapping("/api/assignment/attempt")
public class AssignmentAttemptController {

    private final AssignmentAttemptService attemptService;

    public AssignmentAttemptController(AssignmentAttemptService attemptService) {
        this.attemptService = attemptService;
    }

    @Operation(summary = "Начать попытку")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Попытка начата"),
            @ApiResponse(responseCode = "400", description = "Задание неактивно или попытка уже существует",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является соискателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Задание не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PostMapping("/{assignmentId}/start")
    public AssignmentAttempt start(@PathVariable UUID assignmentId,
                                   @Parameter(hidden = true) @CurrentUser UUID candidateId) {
        return attemptService.start(candidateId, assignmentId);
    }

    @Operation(summary = "Отправить решение")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Решение отправлено"),
            @ApiResponse(responseCode = "400", description = "Попытка не в статусе STARTED или истёк дедлайн",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Попытка принадлежит другому кандидату",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Попытка не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PatchMapping("/{attemptId}/submit")
    public AssignmentAttempt submit(@PathVariable UUID attemptId,
                                    @Valid @RequestBody SubmitAttemptRequest request,
                                    @Parameter(hidden = true) @CurrentUser UUID candidateId) {
        return attemptService.submit(candidateId, attemptId, request);
    }

    @Operation(summary = "Оценить попытку",
            description = "При verdict = PASS автоматически создаётся отклик кандидата на вакансию (идемпотентно).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Попытка оценена"),
            @ApiResponse(responseCode = "400", description = "Попытка не в статусе SUBMITTED",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем или вакансия принадлежит другой компании",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Попытка не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PatchMapping("/{attemptId}/evaluate")
    public AssignmentAttempt evaluate(@PathVariable UUID attemptId,
                                      @Valid @RequestBody EvaluateAttemptRequest request,
                                      @Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return attemptService.evaluate(ownerId, attemptId, request);
    }

    @Operation(summary = "Одна попытка")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Попытка найдена"),
            @ApiResponse(responseCode = "403", description = "Пользователь не имеет доступа к попытке",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Попытка не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @GetMapping("/{attemptId}")
    public AssignmentAttempt getById(@PathVariable UUID attemptId,
                                     @Parameter(hidden = true) @CurrentUser UUID requesterId) {
        return attemptService.getById(requesterId, attemptId);
    }

    @Operation(summary = "Мои попытки")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список получен"),
            @ApiResponse(responseCode = "403", description = "Пользователь не является соискателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @GetMapping("/mine")
    public List<AssignmentAttempt> getMine(@Parameter(hidden = true) @CurrentUser UUID candidateId) {
        return attemptService.getMine(candidateId);
    }

    @Operation(summary = "Все попытки по заданию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список получен"),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем или вакансия принадлежит другой компании",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Задание не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @GetMapping("/by-assignment/{assignmentId}")
    public List<AssignmentAttempt> getByAssignment(@PathVariable UUID assignmentId,
                                                   @Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return attemptService.getByAssignment(ownerId, assignmentId);
    }
}