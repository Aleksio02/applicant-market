package com.maboy.applicantmarket.applicant.controller;

import com.maboy.applicantmarket.applicant.converter.ApplicantPrivacyConverter;
import com.maboy.applicantmarket.applicant.model.request.UpdatePrivacyRequest;
import com.maboy.applicantmarket.applicant.model.response.ApplicantPrivacyResponse;
import com.maboy.applicantmarket.applicant.service.ApplicantPrivacyService;
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
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Applicant", description = "Профиль соискателя")
@RestController
@RequestMapping("/api/applicant/privacy")
@RequiredArgsConstructor
public class ApplicantPrivacyController {

    private final ApplicantPrivacyService service;
    private final ApplicantPrivacyConverter converter;

    @Operation(
            summary = "Получить настройки приватности",
            description = "Возвращает текущие настройки видимости профиля и контактов."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Настройки получены",
                    content = @Content(schema = @Schema(implementation = ApplicantPrivacyResponse.class))),
            @ApiResponse(responseCode = "404", description = "Профиль не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @GetMapping
    public ApplicantPrivacyResponse get(@Parameter(hidden = true) @CurrentUser UUID userId) {
        return converter.toResponse(service.get(userId));
    }

    @Operation(
            summary = "Обновить настройки приватности",
            description = """
                    Обновляет настройки видимости. Поля, не переданные в запросе, не изменяются.
                    Изменение visibleInSearch публикует событие ApplicantPrivacyChanged.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Настройки обновлены",
                    content = @Content(schema = @Schema(implementation = ApplicantPrivacyResponse.class))),
            @ApiResponse(responseCode = "404", description = "Профиль не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PutMapping
    public ApplicantPrivacyResponse update(
            @Parameter(hidden = true) @CurrentUser UUID userId,
            @RequestBody UpdatePrivacyRequest request
    ) {
        return converter.toResponse(service.update(userId, request));
    }
}