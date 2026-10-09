package com.maboy.applicantmarket.applicant.controller;

import com.maboy.applicantmarket.applicant.converter.ApplicantProfileConverter;
import com.maboy.applicantmarket.applicant.model.ApplicantProfile;
import com.maboy.applicantmarket.applicant.model.request.UpdateApplicantProfileRequest;
import com.maboy.applicantmarket.applicant.model.response.ApplicantProfileCompletenessResponse;
import com.maboy.applicantmarket.applicant.model.response.ApplicantProfileResponse;
import com.maboy.applicantmarket.applicant.service.ApplicantProfileCompletenessService;
import com.maboy.applicantmarket.applicant.service.ApplicantProfileService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Applicant", description = "Профиль соискателя")
@RestController
@RequestMapping("/api/applicant/profile")
@RequiredArgsConstructor
public class ApplicantProfileController {

    private final ApplicantProfileService profileService;
    private final ApplicantProfileCompletenessService completenessService;
    private final ApplicantProfileConverter converter;

    @Operation(
            summary = "Получить свой профиль",
            description = """
                    Возвращает профиль текущего соискателя.
                    Если профиля ещё нет, он создаётся пустым со статусом DRAFT.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Профиль получен",
                    content = @Content(schema = @Schema(implementation = ApplicantProfileResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @GetMapping
    public ApplicantProfileResponse get(@Parameter(hidden = true) @CurrentUser UUID userId) {
        ApplicantProfile profile = profileService.getOrCreate(userId);
        return converter.toResponse(profile);
    }

    @Operation(
            summary = "Обновить профиль",
            description = "Обновляет поля профиля. Пустые поля не изменяются."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Профиль обновлён",
                    content = @Content(schema = @Schema(implementation = ApplicantProfileResponse.class))),
            @ApiResponse(responseCode = "404", description = "Профиль не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PutMapping
    public ApplicantProfileResponse update(
            @Parameter(hidden = true) @CurrentUser UUID userId,
            @RequestBody UpdateApplicantProfileRequest request
    ) {
        ApplicantProfile profile = profileService.update(userId, request);
        return converter.toResponse(profile);
    }

    @Operation(
            summary = "Активировать профиль",
            description = "Переводит профиль в статус ACTIVE — он становится виден работодателям в поиске."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Профиль активирован",
                    content = @Content(schema = @Schema(implementation = ApplicantProfileResponse.class))),
            @ApiResponse(responseCode = "404", description = "Профиль не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PostMapping("/activate")
    public ApplicantProfileResponse activate(@Parameter(hidden = true) @CurrentUser UUID userId) {
        return converter.toResponse(profileService.activate(userId));
    }

    @Operation(
            summary = "Скрыть профиль",
            description = "Переводит профиль в статус HIDDEN — работодатели его не видят."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Профиль скрыт",
                    content = @Content(schema = @Schema(implementation = ApplicantProfileResponse.class))),
            @ApiResponse(responseCode = "404", description = "Профиль не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PostMapping("/hide")
    public ApplicantProfileResponse hide(@Parameter(hidden = true) @CurrentUser UUID userId) {
        return converter.toResponse(profileService.hide(userId));
    }

    @Operation(
            summary = "Процент заполненности профиля",
            description = """
                    Возвращает процент заполненности профиля и список незаполненных блоков.
                    Используется фронтом, чтобы показать прогресс «заполните профиль до 100 %».
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Процент получен",
                    content = @Content(schema = @Schema(implementation = ApplicantProfileCompletenessResponse.class))),
            @ApiResponse(responseCode = "404", description = "Профиль не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @GetMapping("/completeness")
    public ApplicantProfileCompletenessResponse completeness(@Parameter(hidden = true) @CurrentUser UUID userId) {
        return completenessService.compute(userId);
    }
}