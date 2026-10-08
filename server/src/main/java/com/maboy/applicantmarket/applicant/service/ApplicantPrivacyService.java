package com.maboy.applicantmarket.applicant.service;

import com.maboy.applicantmarket.applicant.api.event.ApplicantPrivacyChanged;
import com.maboy.applicantmarket.applicant.converter.ApplicantPrivacyConverter;
import com.maboy.applicantmarket.applicant.dao.ApplicantPrivacySettingsDao;
import com.maboy.applicantmarket.applicant.dao.dto.ApplicantPrivacySettingsDto;
import com.maboy.applicantmarket.applicant.model.ApplicantPrivacySettings;
import com.maboy.applicantmarket.applicant.model.request.UpdatePrivacyRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicantPrivacyService {

    private final ApplicantPrivacySettingsDao dao;
    private final ApplicantProfileService profileService;
    private final ApplicantPrivacyConverter converter;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ApplicantPrivacySettings get(UUID userId) {
        UUID applicantId = profileService.getOrCreate(userId).getId();
        ApplicantPrivacySettingsDto entity = dao.findById(applicantId)
            .orElseGet(() -> dao.save(ApplicantPrivacySettingsDto.builder()
                .applicantId(applicantId)
                .build()));
        return converter.toModel(entity);
    }

    @Transactional
    public ApplicantPrivacySettings update(UUID userId, UpdatePrivacyRequest request) {
        UUID applicantId = profileService.getOrCreate(userId).getId();
        ApplicantPrivacySettingsDto entity = dao.findById(applicantId)
            .orElseThrow(() -> new IllegalStateException("Privacy settings not found"));
        boolean wasVisible = Boolean.TRUE.equals(entity.getVisibleInSearch());
        if (request.getVisibleInSearch() != null) {
            entity.setVisibleInSearch(request.getVisibleInSearch());
        }
        if (request.getAllowInvitations() != null) {
            entity.setAllowInvitations(request.getAllowInvitations());
        }
        if (request.getShowContactsAfterAccept() != null) {
            entity.setShowContactsAfterAccept(request.getShowContactsAfterAccept());
        }
        if (request.getShowFspAchievements() != null) {
            entity.setShowFspAchievements(request.getShowFspAchievements());
        }

        ApplicantPrivacySettingsDto saved = dao.save(entity);
        boolean isVisible = Boolean.TRUE.equals(saved.getVisibleInSearch());

        if (wasVisible != isVisible) {
            eventPublisher.publishEvent(new ApplicantPrivacyChanged(
                applicantId, Boolean.TRUE.equals(saved.getVisibleInSearch())));
        }
        return converter.toModel(saved);
    }
}