package com.maboy.applicantmarket.applicant.controller;

import com.maboy.applicantmarket.applicant.converter.ApplicantExperienceConverter;
import com.maboy.applicantmarket.applicant.model.request.AddExperienceRequest;
import com.maboy.applicantmarket.applicant.model.request.UpdateExperienceRequest;
import com.maboy.applicantmarket.applicant.model.response.ApplicantExperienceResponse;
import com.maboy.applicantmarket.applicant.service.ApplicantExperienceService;
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
@RequestMapping("/api/applicant/experiences")
@RequiredArgsConstructor
public class ApplicantExperienceController {

    private final ApplicantExperienceService service;
    private final ApplicantExperienceConverter converter;

    @Operation(
            summary = "Список опыта работы",
            description = "Возвращает все места работы текущего соискателя, отсортированные по sortOrder."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список получен"),
            @ApiResponse(responseCode = "404", description = "Профиль не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @GetMapping
    public List<ApplicantExperienceResponse> list(@Parameter(hidden = true) @CurrentUser UUID userId) {
        return service.list(userId).stream().map(converter::toResponse).toList();
    }

    @Operation(
            summary = "Добавить опыт работы",
            description = "Добавляет запись об опыте работы."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Опыт добавлен",
                    content = @Content(schema = @Schema(implementation = ApplicantExperienceResponse.class))),
            @ApiResponse(responseCode = "404", description = "Профиль не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PostMapping
    public ApplicantExperienceResponse add(
            @Parameter(hidden = true) @CurrentUser UUID userId,
            @RequestBody AddExperienceRequest request
    ) {
        return converter.toResponse(service.add(userId, request));
    }

    @Operation(
            summary = "Обновить опыт работы",
            description = "Обновляет запись об опыте работы. Пустые поля не изменяются."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Опыт обновлён",
                    content = @Content(schema = @Schema(implementation = ApplicantExperienceResponse.class))),
            @ApiResponse(responseCode = "404", description = "Опыт не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PutMapping("/{id}")
    public ApplicantExperienceResponse update(
            @Parameter(hidden = true) @CurrentUser UUID userId,
            @PathVariable UUID id,
            @RequestBody UpdateExperienceRequest request
    ) {
        return converter.toResponse(service.update(userId, id, request));
    }

    @Operation(
            summary = "Удалить опыт работы",
            description = "Удаляет запись об опыте работы."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Опыт удалён"),
            @ApiResponse(responseCode = "404", description = "Опыт не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @DeleteMapping("/{id}")
    public void delete(@Parameter(hidden = true) @CurrentUser UUID userId, @PathVariable UUID id) {
        service.delete(userId, id);
    }
}