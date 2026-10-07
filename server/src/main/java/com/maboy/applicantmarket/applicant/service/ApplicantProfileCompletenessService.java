package com.maboy.applicantmarket.applicant.service;

import com.maboy.applicantmarket.applicant.dao.ApplicantEducationDao;
import com.maboy.applicantmarket.applicant.dao.ApplicantExperienceDao;
import com.maboy.applicantmarket.applicant.dao.ApplicantProfileDao;
import com.maboy.applicantmarket.applicant.dao.ApplicantSkillDao;
import com.maboy.applicantmarket.applicant.dao.dto.ApplicantProfileDto;
import com.maboy.applicantmarket.commons.exception.ApplicantNotFoundException;
import com.maboy.applicantmarket.applicant.model.response.ApplicantProfileCompletenessResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicantProfileCompletenessService {

    private final ApplicantProfileDao profileRepository;
    private final ApplicantSkillDao skillRepository;
    private final ApplicantExperienceDao experienceRepository;
    private final ApplicantEducationDao educationRepository;

    @Transactional(readOnly = true)
    public ApplicantProfileCompletenessResponse compute(UUID userId) {
        ApplicantProfileDto profile = profileRepository.findByUserId(userId)
            .orElseThrow(() -> new ApplicantNotFoundException("Profile not found"));
        UUID applicantId = profile.getId();

        List<String> missing = new ArrayList<>();
        int done = 0;
        int total = 8;

        if (isFilled(profile.getFirstName()) && isFilled(profile.getLastName())) {
            done++;
        } else {
            missing.add("fullName");
        }

        if (isFilled(profile.getAbout())) {
            done++;
        } else {
            missing.add("about");
        }

        if (isFilled(profile.getPhone())) {
            done++;
        } else {
            missing.add("phone");
        }

        if (profile.getExperienceYears() != null) {
            done++;
        } else {
            missing.add("experienceYears");
        }

        if (!experienceRepository.findAllByApplicantIdOrderBySortOrderAsc(applicantId).isEmpty()) {
            done++;
        } else {
            missing.add("experience");
        }

        if (!educationRepository.findAllByApplicantId(applicantId).isEmpty()) {
            done++;
        } else {
            missing.add("education");
        }

        var skills = skillRepository.findAllByApplicantId(applicantId);
        if (!skills.isEmpty()) {
            done++;
        } else {
            missing.add("skills");
        }

        boolean hasPrimaryVerified = skills.stream().anyMatch(s ->
            Boolean.TRUE.equals(s.getIsPrimary()) && s.getVerifiedGradeId() != null);
        if (hasPrimaryVerified) {
            done++;
        } else {
            missing.add("primaryVerifiedSkill");
        }

        int percent = total == 0 ? 0 : (int) Math.round(done * 100.0 / total);
        return new ApplicantProfileCompletenessResponse(percent, missing);
    }

    private boolean isFilled(String s) {
        return s != null && !s.isBlank();
    }
}