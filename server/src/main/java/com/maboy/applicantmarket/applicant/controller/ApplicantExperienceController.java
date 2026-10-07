package com.maboy.applicantmarket.applicant.controller;

import com.maboy.applicantmarket.applicant.converter.ApplicantExperienceConverter;
import com.maboy.applicantmarket.applicant.model.request.AddExperienceRequest;
import com.maboy.applicantmarket.applicant.model.request.UpdateExperienceRequest;
import com.maboy.applicantmarket.applicant.model.response.ApplicantExperienceResponse;
import com.maboy.applicantmarket.applicant.service.ApplicantExperienceService;
import com.maboy.applicantmarket.auth.config.annotation.CurrentUser;
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

@RestController
@RequestMapping("/api/applicant/experiences")
@RequiredArgsConstructor
public class ApplicantExperienceController {

    private final ApplicantExperienceService service;
    private final ApplicantExperienceConverter converter;

    @GetMapping
    public List<ApplicantExperienceResponse> list(@CurrentUser UUID userId) {
        return service.list(userId).stream().map(converter::toResponse).toList();
    }

    @PostMapping
    public ApplicantExperienceResponse add(
        @CurrentUser UUID userId,
        @RequestBody AddExperienceRequest request
    ) {
        return converter.toResponse(service.add(userId, request));
    }

    @PutMapping("/{id}")
    public ApplicantExperienceResponse update(
        @CurrentUser UUID userId,
        @PathVariable UUID id,
        @RequestBody UpdateExperienceRequest request
    ) {
        return converter.toResponse(service.update(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public void delete(@CurrentUser UUID userId, @PathVariable UUID id) {
        service.delete(userId, id);
    }
}