package com.maboy.applicantmarket.interaction.controller;

import com.maboy.applicantmarket.auth.config.annotation.CurrentUser;
import com.maboy.applicantmarket.commons.model.response.ErrorResponse;
import com.maboy.applicantmarket.interaction.model.Interaction;
import com.maboy.applicantmarket.interaction.model.request.CreateApplicationRequest;
import com.maboy.applicantmarket.interaction.model.request.CreateInvitationRequest;
import com.maboy.applicantmarket.interaction.model.response.ContactInfo;
import com.maboy.applicantmarket.interaction.service.InteractionService;
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

@Tag(name = "Interaction", description = "Приглашения и отклики")
@RestController
@RequestMapping("/api/interaction")
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    // ---------- Приглашения ----------

    @Operation(summary = "Создать приглашение кандидату")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Приглашение создано"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации, кандидат неактивен, вакансия не опубликована, " +
                    "не указана зарплата или уже есть активное приглашение",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем или вакансия принадлежит другой компании",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Кандидат или вакансия не найдены",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PostMapping("/invitation")
    public Interaction createInvitation(@Valid @RequestBody CreateInvitationRequest request,
                                        @Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return interactionService.createInvitation(ownerId, request);
    }

    @Operation(summary = "Исходящие приглашения")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список получен"),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Компания не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @GetMapping("/invitation/outgoing")
    public List<Interaction> getOutgoingInvitations(@Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return interactionService.getOutgoingInvitations(ownerId);
    }

    @Operation(summary = "Входящие приглашения")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список получен"),
            @ApiResponse(responseCode = "403", description = "Пользователь не является соискателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @GetMapping("/invitation/incoming")
    public List<Interaction> getIncomingInvitations(@Parameter(hidden = true) @CurrentUser UUID candidateId) {
        return interactionService.getIncomingInvitations(candidateId);
    }

    @Operation(summary = "Принять приглашение",
            description = "При принятии работодателю раскрываются контактные данные кандидата.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Приглашение принято"),
            @ApiResponse(responseCode = "400", description = "Приглашение уже в терминальном статусе",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Приглашение не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PatchMapping("/invitation/{id}/accept")
    public Interaction acceptInvitation(@PathVariable UUID id,
                                        @Parameter(hidden = true) @CurrentUser UUID candidateId) {
        return interactionService.acceptInvitation(candidateId, id);
    }

    @Operation(summary = "Отклонить приглашение")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Приглашение отклонено"),
            @ApiResponse(responseCode = "400", description = "Приглашение уже в терминальном статусе",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Приглашение не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PatchMapping("/invitation/{id}/reject")
    public Interaction rejectInvitation(@PathVariable UUID id,
                                        @Parameter(hidden = true) @CurrentUser UUID candidateId) {
        return interactionService.rejectInvitation(candidateId, id);
    }

    @Operation(summary = "Отозвать приглашение")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Приглашение отозвано"),
            @ApiResponse(responseCode = "400", description = "Приглашение уже в терминальном статусе",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Приглашение не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PatchMapping("/invitation/{id}/withdraw")
    public Interaction withdrawInvitation(@PathVariable UUID id,
                                          @Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return interactionService.withdrawInvitation(ownerId, id);
    }

    // ---------- Отклики ----------

    @Operation(summary = "Откликнуться на вакансию",
            description = "Отклик создаётся от имени кандидата; контакты раскрываются работодателю сразу.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Отклик создан"),
            @ApiResponse(responseCode = "400", description = "Вакансия не опубликована или уже есть активный отклик",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является соискателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Вакансия не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PostMapping("/application/vacancy/{vacancyId}")
    public Interaction createApplication(@PathVariable UUID vacancyId,
                                         @RequestBody(required = false) CreateApplicationRequest request,
                                         @Parameter(hidden = true) @CurrentUser UUID candidateId) {
        if (request == null) {
            request = new CreateApplicationRequest();
        }
        return interactionService.createApplication(candidateId, vacancyId, request);
    }

    @Operation(summary = "Мои отклики")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список получен"),
            @ApiResponse(responseCode = "403", description = "Пользователь не является соискателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @GetMapping("/application/mine")
    public List<Interaction> getMyApplications(@Parameter(hidden = true) @CurrentUser UUID candidateId) {
        return interactionService.getMyApplications(candidateId);
    }

    @Operation(summary = "Входящие отклики")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список получен"),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Компания не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @GetMapping("/application/incoming")
    public List<Interaction> getIncomingApplications(@Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return interactionService.getIncomingApplications(ownerId);
    }

    @Operation(summary = "Принять отклик")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Отклик принят"),
            @ApiResponse(responseCode = "400", description = "Отклик уже в терминальном статусе",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Отклик не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PatchMapping("/application/{id}/accept")
    public Interaction acceptApplication(@PathVariable UUID id,
                                         @Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return interactionService.acceptApplication(ownerId, id);
    }

    @Operation(summary = "Отклонить отклик")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Отклик отклонён"),
            @ApiResponse(responseCode = "400", description = "Отклик уже в терминальном статусе",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Отклик не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PatchMapping("/application/{id}/reject")
    public Interaction rejectApplication(@PathVariable UUID id,
                                         @Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return interactionService.rejectApplication(ownerId, id);
    }

    @Operation(summary = "Отозвать отклик")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Отклик отозван"),
            @ApiResponse(responseCode = "400", description = "Отклик уже в терминальном статусе",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Отклик не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PatchMapping("/application/{id}/withdraw")
    public Interaction withdrawApplication(@PathVariable UUID id,
                                           @Parameter(hidden = true) @CurrentUser UUID candidateId) {
        return interactionService.withdrawApplication(candidateId, id);
    }

    // ---------- Общее ----------

    @Operation(summary = "Одно взаимодействие",
            description = "При просмотре входящего приглашения кандидатом статус SENT переводится в VIEWED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Взаимодействие найдено"),
            @ApiResponse(responseCode = "403", description = "Пользователь не имеет доступа к взаимодействию",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Взаимодействие не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @GetMapping("/{id}")
    public Interaction getById(@PathVariable UUID id,
                               @Parameter(hidden = true) @CurrentUser UUID requesterId) {
        return interactionService.getById(requesterId, id);
    }

    @Operation(summary = "Контакты кандидата",
            description = "Доступно работодателю только после того, как кандидат принял приглашение или откликнулся сам.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Контакты получены"),
            @ApiResponse(responseCode = "403", description = "Контакты ещё не раскрыты или пользователь не является работодателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Взаимодействие или кандидат не найдены",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @GetMapping("/{id}/contacts")
    public ContactInfo getContacts(@PathVariable UUID id,
                                   @Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return interactionService.getContacts(ownerId, id);
    }
}