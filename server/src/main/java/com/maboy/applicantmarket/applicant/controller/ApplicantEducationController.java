package com.maboy.applicantmarket.applicant.controller;

import com.maboy.applicantmarket.applicant.converter.ApplicantEducationConverter;
import com.maboy.applicantmarket.applicant.model.request.AddEducationRequest;
import com.maboy.applicantmarket.applicant.model.request.UpdateEducationRequest;
import com.maboy.applicantmarket.applicant.model.response.ApplicantEducationResponse;
import com.maboy.applicantmarket.applicant.service.ApplicantEducationService;
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
@RequestMapping("/api/applicant/educations")
@RequiredArgsConstructor
public class ApplicantEducationController {

    private final ApplicantEducationService service;
    private final ApplicantEducationConverter converter;

    @GetMapping
    public List<ApplicantEducationResponse> list(@CurrentUser UUID userId) {
        return service.list(userId).stream().map(converter::toResponse).toList();
    }

    @PostMapping
    public ApplicantEducationResponse add(
        @CurrentUser UUID userId,
        @RequestBody AddEducationRequest request
    ) {
        return converter.toResponse(service.add(userId, request));
    }

    @PutMapping("/{id}")
    public ApplicantEducationResponse update(
        @CurrentUser UUID userId,
        @PathVariable UUID id,
        @RequestBody UpdateEducationRequest request
    ) {
        return converter.toResponse(service.update(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public void delete(@CurrentUser UUID userId, @PathVariable UUID id) {
        service.delete(userId, id);
    }
}