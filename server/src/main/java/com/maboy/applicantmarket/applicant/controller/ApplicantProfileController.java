package com.maboy.applicantmarket.applicant.controller;

import com.maboy.applicantmarket.applicant.converter.ApplicantProfileConverter;
import com.maboy.applicantmarket.applicant.model.ApplicantProfile;
import com.maboy.applicantmarket.applicant.model.request.UpdateApplicantProfileRequest;
import com.maboy.applicantmarket.applicant.model.response.ApplicantProfileCompletenessResponse;
import com.maboy.applicantmarket.applicant.model.response.ApplicantProfileResponse;
import com.maboy.applicantmarket.applicant.service.ApplicantProfileCompletenessService;
import com.maboy.applicantmarket.applicant.service.ApplicantProfileService;
import com.maboy.applicantmarket.auth.config.annotation.CurrentUser;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/applicant/profile")
@RequiredArgsConstructor
public class ApplicantProfileController {

    private final ApplicantProfileService profileService;
    private final ApplicantProfileCompletenessService completenessService;
    private final ApplicantProfileConverter converter;

    @GetMapping
    public ApplicantProfileResponse get(@CurrentUser UUID userId) {
        ApplicantProfile profile = profileService.getOrCreate(userId);
        return converter.toResponse(profile);
    }

    @PutMapping
    public ApplicantProfileResponse update(
        @CurrentUser UUID userId,
        @RequestBody UpdateApplicantProfileRequest request
    ) {
        ApplicantProfile profile = profileService.update(userId, request);
        return converter.toResponse(profile);
    }

    @PostMapping("/activate")
    public ApplicantProfileResponse activate(@CurrentUser UUID userId) {
        return converter.toResponse(profileService.activate(userId));
    }

    @PostMapping("/hide")
    public ApplicantProfileResponse hide(@CurrentUser UUID userId) {
        return converter.toResponse(profileService.hide(userId));
    }

    @GetMapping("/completeness")
    public ApplicantProfileCompletenessResponse completeness(@CurrentUser UUID userId) {
        return completenessService.compute(userId);
    }
}