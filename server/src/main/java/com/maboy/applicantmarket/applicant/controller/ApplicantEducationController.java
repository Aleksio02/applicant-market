package com.maboy.applicantmarket.applicant.controller;

import com.maboy.applicantmarket.applicant.converter.ApplicantEducationConverter;
import com.maboy.applicantmarket.applicant.model.request.AddEducationRequest;
import com.maboy.applicantmarket.applicant.model.request.UpdateEducationRequest;
import com.maboy.applicantmarket.applicant.model.response.ApplicantEducationResponse;
import com.maboy.applicantmarket.applicant.service.ApplicantEducationService;
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
@RequestMapping("/api/applicant/educations")
@RequiredArgsConstructor
public class ApplicantEducationController {

    private final ApplicantEducationService service;
    private final ApplicantEducationConverter converter;

    @Operation(
            summary = "Список образования",
            description = "Возвращает все записи об образовании текущего соискателя."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список получен"),
            @ApiResponse(responseCode = "404", description = "Профиль не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @GetMapping
    public List<ApplicantEducationResponse> list(@Parameter(hidden = true) @CurrentUser UUID userId) {
        return service.list(userId).stream().map(converter::toResponse).toList();
    }

    @Operation(
            summary = "Добавить образование",
            description = "Добавляет запись об образовании."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Образование добавлено",
                    content = @Content(schema = @Schema(implementation = ApplicantEducationResponse.class))),
            @ApiResponse(responseCode = "404", description = "Профиль не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PostMapping
    public ApplicantEducationResponse add(
            @Parameter(hidden = true) @CurrentUser UUID userId,
            @RequestBody AddEducationRequest request
    ) {
        return converter.toResponse(service.add(userId, request));
    }

    @Operation(
            summary = "Обновить образование",
            description = "Обновляет запись об образовании. Пустые поля не изменяются."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Образование обновлено",
                    content = @Content(schema = @Schema(implementation = ApplicantEducationResponse.class))),
            @ApiResponse(responseCode = "404", description = "Образование не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PutMapping("/{id}")
    public ApplicantEducationResponse update(
            @Parameter(hidden = true) @CurrentUser UUID userId,
            @PathVariable UUID id,
            @RequestBody UpdateEducationRequest request
    ) {
        return converter.toResponse(service.update(userId, id, request));
    }

    @Operation(
            summary = "Удалить образование",
            description = "Удаляет запись об образовании."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Образование удалено"),
            @ApiResponse(responseCode = "404", description = "Образование не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @DeleteMapping("/{id}")
    public void delete(@Parameter(hidden = true) @CurrentUser UUID userId, @PathVariable UUID id) {
        service.delete(userId, id);
    }
}