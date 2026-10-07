package com.maboy.applicantmarket.applicant.service;

import com.maboy.applicantmarket.applicant.api.ApplicantModuleApi;
import com.maboy.applicantmarket.applicant.api.model.ApplicantPrimarySkill;
import com.maboy.applicantmarket.applicant.api.model.ApplicantSkillRef;
import com.maboy.applicantmarket.applicant.api.model.ApplicantSummary;
import com.maboy.applicantmarket.applicant.api.model.FspAchievementRef;
import com.maboy.applicantmarket.applicant.dao.ApplicantPrivacySettingsDao;
import com.maboy.applicantmarket.applicant.dao.ApplicantProfileDao;
import com.maboy.applicantmarket.applicant.dao.ApplicantSkillDao;
import com.maboy.applicantmarket.applicant.dao.FspAchievementStubDao;
import com.maboy.applicantmarket.applicant.dao.dto.ApplicantPrivacySettingsDto;
import com.maboy.applicantmarket.applicant.dao.dto.ApplicantProfileDto;
import com.maboy.applicantmarket.applicant.dao.dto.ApplicantSkillDto;
import com.maboy.applicantmarket.commons.exception.ApplicantNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicantModuleApiImpl implements ApplicantModuleApi {

    private final ApplicantProfileDao profileDao;
    private final ApplicantSkillDao skillDao;
    private final ApplicantPrivacySettingsDao privacyDao;
    private final FspAchievementStubDao fspDao;

    @Override
    public ApplicantSummary getSummary(UUID applicantId) {
        ApplicantProfileDto profile = profileDao.findById(applicantId)
                .orElseThrow(() -> new ApplicantNotFoundException("Applicant not found: " + applicantId));

        boolean visible = privacyDao.findById(applicantId)
                .map(ApplicantPrivacySettingsDto::getVisibleInSearch)
                .orElse(true);

        Optional<ApplicantSkillDto> primary = skillDao.findByApplicantIdAndIsPrimaryTrue(applicantId);

        return ApplicantSummary.builder()
                .applicantId(profile.getId())
                .displayName(buildDisplayName(profile))
                .primarySkillId(primary.map(ApplicantSkillDto::getSkillId).orElse(null))
                .primaryGradeId(primary.map(ApplicantSkillDto::getVerifiedGradeId).orElse(null))
                .experienceYears(profile.getExperienceYears())
                .visibleInSearch(visible)
                .build();
    }

    @Override
    public List<ApplicantSkillRef> getVerifiedSkills(UUID applicantId) {
        return skillDao.findAllByApplicantId(applicantId).stream()
                .filter(s -> s.getVerifiedGradeId() != null)
                .map(s -> ApplicantSkillRef.builder()
                        .skillId(s.getSkillId())
                        .gradeId(s.getVerifiedGradeId())
                        .primary(Boolean.TRUE.equals(s.getIsPrimary()))
                        .build())
                .toList();
    }

    @Override
    public Optional<ApplicantPrimarySkill> getPrimarySkill(UUID applicantId) {
        return skillDao.findByApplicantIdAndIsPrimaryTrue(applicantId)
                .map(s -> ApplicantPrimarySkill.builder()
                        .skillId(s.getSkillId())
                        .gradeId(s.getVerifiedGradeId())
                        .build());
    }

    @Override
    public boolean hasFspHistory(UUID applicantId) {
        return !fspDao.findAllByApplicantIdOrderByEventDateDesc(applicantId).isEmpty();
    }

    @Override
    public List<FspAchievementRef> getFspAchievements(UUID applicantId) {
        return fspDao.findAllByApplicantIdOrderByEventDateDesc(applicantId).stream()
                .map(e -> FspAchievementRef.builder()
                        .eventName(e.getEventName())
                        .eventDate(e.getEventDate())
                        .place(e.getPlace())
                        .category(e.getCategory())
                        .build())
                .toList();
    }

    @Override
    public List<UUID> findApplicantIdsBySkillAndGrade(UUID skillId, UUID gradeId) {
        if (skillId == null || gradeId == null) {
            return List.of();
        }
        return skillDao.findApplicantIdsBySkillAndGrade(skillId, gradeId);
    }

    private String buildDisplayName(ApplicantProfileDto p) {
        StringBuilder sb = new StringBuilder();
        if (p.getLastName() != null) sb.append(p.getLastName());
        if (p.getFirstName() != null) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(p.getFirstName());
        }
        return sb.toString().trim();
    }
}