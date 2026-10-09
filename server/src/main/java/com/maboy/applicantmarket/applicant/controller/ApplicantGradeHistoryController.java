package com.maboy.applicantmarket.applicant.controller;

import com.maboy.applicantmarket.applicant.converter.GradeHistoryConverter;
import com.maboy.applicantmarket.applicant.dao.GradeChangeHistoryDao;
import com.maboy.applicantmarket.applicant.model.response.GradeHistoryResponse;
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
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Applicant", description = "Профиль соискателя")
@RestController
@RequestMapping("/api/applicant/grade-history")
@RequiredArgsConstructor
public class ApplicantGradeHistoryController {

    private final GradeChangeHistoryDao repository;
    private final ApplicantProfileService profileService;
    private final GradeHistoryConverter converter;

    @Operation(
            summary = "История смены грейда",
            description = """
                    Возвращает историю смены грейда по всем навыкам соискателя,
                    отсортированную по дате смены (сначала новые).
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "История получена"),
            @ApiResponse(responseCode = "404", description = "Профиль не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @GetMapping
    @Transactional(readOnly = true)
    public List<GradeHistoryResponse> list(@Parameter(hidden = true) @CurrentUser UUID userId) {
        UUID applicantId = profileService.getByUserId(userId).getId();
        return repository.findAllByApplicantIdOrderByChangedAtDesc(applicantId).stream()
                .map(converter::toResponse).toList();
    }
}