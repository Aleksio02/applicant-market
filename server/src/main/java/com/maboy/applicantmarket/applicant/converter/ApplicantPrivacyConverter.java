package com.maboy.applicantmarket.applicant.converter;

import com.maboy.applicantmarket.applicant.dao.dto.ApplicantPrivacySettingsDto;
import com.maboy.applicantmarket.applicant.model.ApplicantPrivacySettings;
import com.maboy.applicantmarket.applicant.model.response.ApplicantPrivacyResponse;
import org.springframework.stereotype.Component;

@Component
public class ApplicantPrivacyConverter {

    public ApplicantPrivacySettings toModel(ApplicantPrivacySettingsDto e) {
        if (e == null) {
            return null;
        }
        return ApplicantPrivacySettings.builder()
            .applicantId(e.getApplicantId())
            .visibleInSearch(e.getVisibleInSearch())
            .allowInvitations(e.getAllowInvitations())
            .showContactsAfterAccept(e.getShowContactsAfterAccept())
            .showFspAchievements(e.getShowFspAchievements())
            .build();
    }

    public ApplicantPrivacySettingsDto toEntity(ApplicantPrivacySettings m) {
        if (m == null) {
            return null;
        }
        ApplicantPrivacySettingsDto e = ApplicantPrivacySettingsDto.builder()
            .applicantId(m.getApplicantId())
            .visibleInSearch(m.getVisibleInSearch())
            .allowInvitations(m.getAllowInvitations())
            .showContactsAfterAccept(m.getShowContactsAfterAccept())
            .showFspAchievements(m.getShowFspAchievements())
            .build();
        return e;
    }

    public ApplicantPrivacyResponse toResponse(ApplicantPrivacySettings m) {
        if (m == null) {
            return null;
        }
        ApplicantPrivacyResponse r = new ApplicantPrivacyResponse();
        r.setVisibleInSearch(m.getVisibleInSearch());
        r.setAllowInvitations(m.getAllowInvitations());
        r.setShowContactsAfterAccept(m.getShowContactsAfterAccept());
        r.setShowFspAchievements(m.getShowFspAchievements());
        return r;
    }
}