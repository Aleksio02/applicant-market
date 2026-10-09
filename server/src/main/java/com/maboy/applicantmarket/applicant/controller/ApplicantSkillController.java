package com.maboy.applicantmarket.applicant.controller;

import com.maboy.applicantmarket.applicant.converter.ApplicantSkillConverter;
import com.maboy.applicantmarket.applicant.model.request.AddApplicantSkillRequest;
import com.maboy.applicantmarket.applicant.model.request.UpdateApplicantSkillRequest;
import com.maboy.applicantmarket.applicant.model.response.ApplicantSkillResponse;
import com.maboy.applicantmarket.applicant.service.ApplicantSkillService;
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
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Applicant", description = "Профиль соискателя")
@RestController
@RequestMapping("/api/applicant/skills")
@RequiredArgsConstructor
public class ApplicantSkillController {

    private final ApplicantSkillService skillService;
    private final ApplicantSkillConverter converter;

    @Operation(
            summary = "Список навыков",
            description = "Возвращает все навыки текущего соискателя."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список получен"),
            @ApiResponse(responseCode = "404", description = "Профиль не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @GetMapping
    public List<ApplicantSkillResponse> list(@Parameter(hidden = true) @CurrentUser UUID userId) {
        return skillService.listByUserId(userId).stream()
                .map(converter::toResponse).toList();
    }

    @Operation(
            summary = "Добавить навык",
            description = "Добавляет навык в профиль. Навык берётся из справочника по skillId."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Навык добавлен",
                    content = @Content(schema = @Schema(implementation = ApplicantSkillResponse.class))),
            @ApiResponse(responseCode = "400", description = "Навык уже добавлен",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PostMapping
    public ApplicantSkillResponse add(
            @Parameter(hidden = true) @CurrentUser UUID userId,
            @RequestBody AddApplicantSkillRequest request
    ) {
        return converter.toResponse(skillService.add(userId, request));
    }

    @Operation(
            summary = "Обновить навык",
            description = "Обновляет самооценку уровня и опыт по навыку."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Навык обновлён",
                    content = @Content(schema = @Schema(implementation = ApplicantSkillResponse.class))),
            @ApiResponse(responseCode = "404", description = "Навык не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PutMapping("/{skillId}")
    public ApplicantSkillResponse update(
            @Parameter(hidden = true) @CurrentUser UUID userId,
            @PathVariable UUID skillId,
            @RequestBody UpdateApplicantSkillRequest request
    ) {
        return converter.toResponse(skillService.update(userId, skillId, request));
    }

    @Operation(
            summary = "Удалить навык",
            description = "Удаляет навык из профиля. Основной навык удалить нельзя."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Навык удалён"),
            @ApiResponse(responseCode = "404", description = "Навык не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Нельзя удалить основной навык",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @DeleteMapping("/{skillId}")
    public void remove(
            @Parameter(hidden = true) @CurrentUser UUID userId,
            @PathVariable UUID skillId
    ) {
        skillService.remove(userId, skillId);
    }

    @Operation(
            summary = "Сделать навык основным",
            description = "Назначает навык основным. У профиля может быть только один основной навык."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Навык назначен основным",
                    content = @Content(schema = @Schema(implementation = ApplicantSkillResponse.class))),
            @ApiResponse(responseCode = "404", description = "Навык не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PutMapping("/{skillId}/primary")
    public ApplicantSkillResponse changePrimary(
            @Parameter(hidden = true) @CurrentUser UUID userId,
            @PathVariable UUID skillId
    ) {
        return converter.toResponse(skillService.changePrimary(userId, skillId));
    }
}