package com.maboy.applicantmarket.applicant.controller;

import com.maboy.applicantmarket.applicant.converter.GradeHistoryConverter;
import com.maboy.applicantmarket.applicant.dao.GradeChangeHistoryDao;
import com.maboy.applicantmarket.applicant.model.response.GradeHistoryResponse;
import com.maboy.applicantmarket.applicant.service.ApplicantProfileService;
import com.maboy.applicantmarket.auth.config.annotation.CurrentUser;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/applicant/grade-history")
@RequiredArgsConstructor
public class ApplicantGradeHistoryController {

    private final GradeChangeHistoryDao repository;
    private final ApplicantProfileService profileService;
    private final GradeHistoryConverter converter;

    @GetMapping
    @Transactional(readOnly = true)
    public List<GradeHistoryResponse> list(@CurrentUser UUID userId) {
        UUID applicantId = profileService.getByUserId(userId).getId();
        return repository.findAllByApplicantIdOrderByChangedAtDesc(applicantId).stream()
            .map(converter::toResponse).toList();
    }
}