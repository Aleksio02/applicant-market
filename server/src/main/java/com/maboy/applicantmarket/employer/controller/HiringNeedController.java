package com.maboy.applicantmarket.employer.controller;

import com.maboy.applicantmarket.auth.config.annotation.CurrentUser;
import com.maboy.applicantmarket.commons.model.response.ErrorResponse;
import com.maboy.applicantmarket.commons.model.response.PageResponse;
import com.maboy.applicantmarket.employer.model.HiringNeed;
import com.maboy.applicantmarket.employer.model.request.CreateHiringNeedRequest;
import com.maboy.applicantmarket.employer.model.request.GetHiringNeedListRequest;
import com.maboy.applicantmarket.employer.model.request.UpdateHiringNeedRequest;
import com.maboy.applicantmarket.employer.service.HiringNeedService;
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

@Tag(name = "Employer / Hiring Need", description = "Потребности работодателя в найме")
@RestController
@RequestMapping("/api/employer/hiring-need")
public class HiringNeedController {

    private final HiringNeedService hiringNeedService;

    public HiringNeedController(HiringNeedService hiringNeedService) {
        this.hiringNeedService = hiringNeedService;
    }

    @Operation(summary = "Создать потребность в найме")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Потребность создана"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или salaryFrom > salaryTo",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Компания или справочные данные не найдены",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PostMapping
    public HiringNeed create(@Valid @RequestBody CreateHiringNeedRequest request,
                             @Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return hiringNeedService.create(ownerId, request);
    }

    @Operation(summary = "Список потребностей")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список получен"),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Компания не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @GetMapping
    public PageResponse<HiringNeed> getList(GetHiringNeedListRequest request,
                                            @Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return hiringNeedService.getList(ownerId, request);
    }

    @Operation(summary = "Одна потребность")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Потребность найдена"),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Потребность не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @GetMapping("/{id}")
    public HiringNeed getById(@PathVariable UUID id,
                              @Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return hiringNeedService.getById(ownerId, id);
    }

    @Operation(summary = "Обновить потребность")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Потребность обновлена"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Потребность не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PatchMapping("/{id}")
    public HiringNeed update(@PathVariable UUID id,
                             @Valid @RequestBody UpdateHiringNeedRequest request,
                             @Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return hiringNeedService.update(ownerId, id, request);
    }

    @Operation(summary = "Активировать потребность")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Потребность активирована"),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Потребность не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PatchMapping("/{id}/activate")
    public HiringNeed activate(@PathVariable UUID id,
                               @Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return hiringNeedService.activate(ownerId, id);
    }

    @Operation(summary = "Деактивировать потребность")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Потребность деактивирована"),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Потребность не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PatchMapping("/{id}/deactivate")
    public HiringNeed deactivate(@PathVariable UUID id,
                                 @Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return hiringNeedService.deactivate(ownerId, id);
    }
}