package com.maboy.applicantmarket.applicant.controller;

import com.maboy.applicantmarket.applicant.converter.ApplicantPrivacyConverter;
import com.maboy.applicantmarket.applicant.model.request.UpdatePrivacyRequest;
import com.maboy.applicantmarket.applicant.model.response.ApplicantPrivacyResponse;
import com.maboy.applicantmarket.applicant.service.ApplicantPrivacyService;
import com.maboy.applicantmarket.auth.config.annotation.CurrentUser;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/applicant/privacy")
@RequiredArgsConstructor
public class ApplicantPrivacyController {

    private final ApplicantPrivacyService service;
    private final ApplicantPrivacyConverter converter;

    @GetMapping
    public ApplicantPrivacyResponse get(@CurrentUser UUID userId) {
        return converter.toResponse(service.get(userId));
    }

    @PutMapping
    public ApplicantPrivacyResponse update(
        @CurrentUser UUID userId,
        @RequestBody UpdatePrivacyRequest request
    ) {
        return converter.toResponse(service.update(userId, request));
    }
}