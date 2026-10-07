package com.maboy.applicantmarket.applicant.service;

import com.maboy.applicantmarket.applicant.api.event.ApplicantSkillGradeChanged;
import com.maboy.applicantmarket.applicant.config.GradeCooldownProperties;
import com.maboy.applicantmarket.applicant.dao.ApplicantSkillDao;
import com.maboy.applicantmarket.applicant.dao.GradeChangeHistoryDao;
import com.maboy.applicantmarket.applicant.dao.dto.ApplicantSkillDto;
import com.maboy.applicantmarket.applicant.dao.dto.GradeChangeHistoryDto;
import com.maboy.applicantmarket.commons.exception.GradeChangeCooldownException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GradeChangeService {

    private final ApplicantSkillDao skillDao;
    private final GradeChangeHistoryDao historyDao;
    private final GradeCooldownProperties cooldownProperties;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void applyAssessmentResult(UUID applicantId, UUID skillId, UUID newGradeId) {
        ApplicantSkillDto skill = skillDao
                .findByApplicantIdAndSkillId(applicantId, skillId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Skill " + skillId + " not found for applicant " + applicantId));

        if (newGradeId.equals(skill.getVerifiedGradeId())) {
            return;
        }

        Instant now = Instant.now();
        Duration cooldown = cooldownProperties.getCooldown();

        if (skill.getLastGradeChangeAt() != null
                && Duration.between(skill.getLastGradeChangeAt(), now).compareTo(cooldown) < 0) {
            throw new GradeChangeCooldownException(
                    "Grade change cooldown not elapsed for skill " + skillId);
        }

        UUID fromGradeId = skill.getVerifiedGradeId();

        skill.setVerifiedGradeId(newGradeId);
        skill.setVerifiedAt(now);
        skill.setLastGradeChangeAt(now);
        skillDao.save(skill);

        historyDao.save(GradeChangeHistoryDto.builder()
                .applicantId(applicantId)
                .skillId(skillId)
                .fromGradeId(fromGradeId)
                .toGradeId(newGradeId)
                .reason("ASSESSMENT")
                .build());
        eventPublisher.publishEvent(new ApplicantSkillGradeChanged(
            applicantId, skillId, fromGradeId, newGradeId, now));
    }
}