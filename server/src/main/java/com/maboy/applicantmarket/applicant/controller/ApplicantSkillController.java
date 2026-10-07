package com.maboy.applicantmarket.applicant.controller;

import com.maboy.applicantmarket.applicant.converter.ApplicantSkillConverter;
import com.maboy.applicantmarket.applicant.model.request.AddApplicantSkillRequest;
import com.maboy.applicantmarket.applicant.model.request.UpdateApplicantSkillRequest;
import com.maboy.applicantmarket.applicant.model.response.ApplicantSkillResponse;
import com.maboy.applicantmarket.applicant.service.ApplicantSkillService;
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
@RequestMapping("/api/applicant/skills")
@RequiredArgsConstructor
public class ApplicantSkillController {

    private final ApplicantSkillService skillService;
    private final ApplicantSkillConverter converter;

    @GetMapping
    public List<ApplicantSkillResponse> list(@CurrentUser UUID userId) {
        return skillService.listByUserId(userId).stream()
            .map(converter::toResponse).toList();
    }

    @PostMapping
    public ApplicantSkillResponse add(
        @CurrentUser UUID userId,
        @RequestBody AddApplicantSkillRequest request
    ) {
        return converter.toResponse(skillService.add(userId, request));
    }

    @PutMapping("/{skillId}")
    public ApplicantSkillResponse update(
        @CurrentUser UUID userId,
        @PathVariable UUID skillId,
        @RequestBody UpdateApplicantSkillRequest request
    ) {
        return converter.toResponse(skillService.update(userId, skillId, request));
    }

    @DeleteMapping("/{skillId}")
    public void remove(
        @CurrentUser UUID userId,
        @PathVariable UUID skillId
    ) {
        skillService.remove(userId, skillId);
    }

    @PutMapping("/{skillId}/primary")
    public ApplicantSkillResponse changePrimary(
        @CurrentUser UUID userId,
        @PathVariable UUID skillId
    ) {
        return converter.toResponse(skillService.changePrimary(userId, skillId));
    }
}