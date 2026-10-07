package com.maboy.applicantmarket.applicant.service;

import com.maboy.applicantmarket.applicant.api.event.ApplicantPrimarySkillChanged;
import com.maboy.applicantmarket.applicant.dao.ApplicantSkillDao;
import com.maboy.applicantmarket.applicant.dao.dto.ApplicantSkillDto;
import com.maboy.applicantmarket.applicant.model.ApplicantSkill;
import com.maboy.applicantmarket.commons.exception.ApplicantNotFoundException;
import com.maboy.applicantmarket.applicant.model.request.AddApplicantSkillRequest;
import com.maboy.applicantmarket.applicant.model.request.UpdateApplicantSkillRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicantSkillService {

    private final ApplicantSkillDao skillDao;
    private final ApplicantProfileService profileService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<ApplicantSkill> listByUserId(UUID userId) {
        UUID applicantId = profileService.getByUserId(userId).getId();
        return skillDao.findAllByApplicantId(applicantId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Transactional
    public ApplicantSkill add(UUID userId, AddApplicantSkillRequest request) {
        UUID applicantId = profileService.getOrCreate(userId).getId();
        if (skillDao.existsByApplicantIdAndSkillId(applicantId, request.getSkillId())) {
            throw new IllegalArgumentException("Skill already added: " + request.getSkillId());
        }
        ApplicantSkillDto entity = ApplicantSkillDto.builder()
                .applicantId(applicantId)
                .skillId(request.getSkillId())
                .selfAssessedLevel(request.getSelfAssessedLevel())
                .yearsExperience(request.getYearsExperience())
                .isPrimary(false)
                .build();
        return toModel(skillDao.save(entity));
    }

    @Transactional
    public ApplicantSkill update(UUID userId, UUID skillId, UpdateApplicantSkillRequest request) {
        UUID applicantId = profileService.getByUserId(userId).getId();
        ApplicantSkillDto entity = skillDao.findByApplicantIdAndSkillId(applicantId, skillId)
                .orElseThrow(() -> new ApplicantNotFoundException("Skill not found"));
        if (request.getSelfAssessedLevel() != null) entity.setSelfAssessedLevel(request.getSelfAssessedLevel());
        if (request.getYearsExperience() != null) entity.setYearsExperience(request.getYearsExperience());
        return toModel(skillDao.save(entity));
    }

    @Transactional
    public void remove(UUID userId, UUID skillId) {
        UUID applicantId = profileService.getByUserId(userId).getId();
        ApplicantSkillDto entity = skillDao.findByApplicantIdAndSkillId(applicantId, skillId)
                .orElseThrow(() -> new ApplicantNotFoundException("Skill not found"));
        if (Boolean.TRUE.equals(entity.getIsPrimary())) {
            throw new IllegalStateException("Cannot remove primary skill");
        }
        skillDao.delete(entity);
    }

    @Transactional
    public ApplicantSkill changePrimary(UUID userId, UUID skillId) {
        UUID applicantId = profileService.getByUserId(userId).getId();

        ApplicantSkillDto target = skillDao.findByApplicantIdAndSkillId(applicantId, skillId)
                .orElseThrow(() -> new ApplicantNotFoundException("Skill not found"));

        UUID oldPrimarySkillId = skillDao.findByApplicantIdAndIsPrimaryTrue(applicantId)
                .map(prev -> {
                    prev.setIsPrimary(false);
                    skillDao.saveAndFlush(prev);
                    return prev.getSkillId();
                }).orElse(null);

        target.setIsPrimary(true);

        ApplicantSkillDto saved = skillDao.save(target);

        if(!skillId.equals(oldPrimarySkillId)) {
            eventPublisher.publishEvent(new ApplicantPrimarySkillChanged(
                applicantId, oldPrimarySkillId, skillId
            ));
        }

        return toModel(saved);
    }

    private ApplicantSkill toModel(ApplicantSkillDto e) {
        return ApplicantSkill.builder()
                .id(e.getId())
                .applicantId(e.getApplicantId())
                .skillId(e.getSkillId())
                .selfAssessedLevel(e.getSelfAssessedLevel())
                .verifiedGradeId(e.getVerifiedGradeId())
                .lastGradeChangeAt(e.getLastGradeChangeAt())
                .verifiedAt(e.getVerifiedAt())
                .primary(Boolean.TRUE.equals(e.getIsPrimary()))
                .yearsExperience(e.getYearsExperience())
                .build();
    }
}