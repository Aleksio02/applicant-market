package com.maboy.applicantmarket.applicant.service;

import com.maboy.applicantmarket.applicant.converter.ApplicantProfileConverter;
import com.maboy.applicantmarket.applicant.dao.ApplicantPrivacySettingsDao;
import com.maboy.applicantmarket.applicant.dao.ApplicantProfileDao;
import com.maboy.applicantmarket.applicant.dao.dto.ApplicantPrivacySettingsDto;
import com.maboy.applicantmarket.applicant.dao.dto.ApplicantProfileDto;
import com.maboy.applicantmarket.applicant.model.ApplicantProfile;
import com.maboy.applicantmarket.applicant.model.exception.ApplicantNotFoundException;
import com.maboy.applicantmarket.applicant.model.request.UpdateApplicantProfileRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicantProfileService {

    private final ApplicantProfileDao profileDao;
    private final ApplicantPrivacySettingsDao privacyDao;
    private final ApplicantProfileConverter converter;

    @Transactional
    public ApplicantProfile getOrCreate(UUID userId) {
        return profileDao.findByUserId(userId)
            .map(converter::fromDto)
            .orElseGet(() -> createEmpty(userId));
    }

    private ApplicantProfile createEmpty(UUID userId) {
        ApplicantProfileDto profile = ApplicantProfileDto.builder()
            .userId(userId)
            .firstName("")
            .lastName("")
            .status("DRAFT")
            .build();
        profile = profileDao.save(profile);

        ApplicantPrivacySettingsDto privacy = ApplicantPrivacySettingsDto.builder()
            .applicantId(profile.getId())
            .build();
        privacyDao.save(privacy);

        return converter.fromDto(profile);
    }

    @Transactional(readOnly = true)
    public ApplicantProfile getByUserId(UUID userId) {
        return profileDao.findByUserId(userId)
            .map(converter::fromDto)
            .orElseThrow(() -> new ApplicantNotFoundException("Profile not found for user " + userId));
    }

    @Transactional(readOnly = true)
    public ApplicantProfile getById(UUID applicantId) {
        return profileDao.findById(applicantId)
            .map(converter::fromDto)
            .orElseThrow(() -> new ApplicantNotFoundException("Applicant not found: " + applicantId));
    }

    @Transactional
    public ApplicantProfile update(UUID userId, UpdateApplicantProfileRequest request) {
        ApplicantProfileDto entity = profileDao.findByUserId(userId)
            .orElseThrow(() -> new ApplicantNotFoundException("Profile not found for user " + userId));
        ApplicantProfile model = converter.fromDto(entity);
        converter.applyUpdate(model, request);
        return converter.fromDto(entity);
    }

    @Transactional
    public ApplicantProfile activate(UUID userId) {
        return changeStatus(userId, "ACTIVE");
    }

    @Transactional
    public ApplicantProfile hide(UUID userId) {
        return changeStatus(userId, "HIDDEN");
    }

    private ApplicantProfile changeStatus(UUID userId, String status) {
        ApplicantProfileDto entity = profileDao.findByUserId(userId)
            .orElseThrow(() -> new ApplicantNotFoundException("Profile not found for user " + userId));
        ApplicantProfile model = converter.fromDto(entity);
        model.changeStatus(status);
        entity.setStatus(model.getStatus());
        return converter.fromDto(profileDao.save(entity));
    }
}