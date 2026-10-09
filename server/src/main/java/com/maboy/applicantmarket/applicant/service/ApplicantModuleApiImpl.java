package com.maboy.applicantmarket.applicant.service;

import com.maboy.applicantmarket.applicant.api.ApplicantModuleApi;
import com.maboy.applicantmarket.applicant.api.model.ApplicantPrimarySkill;
import com.maboy.applicantmarket.applicant.api.model.ApplicantSkillRef;
import com.maboy.applicantmarket.applicant.api.model.ApplicantSummary;
import com.maboy.applicantmarket.applicant.api.model.FspAchievementRef;
import com.maboy.applicantmarket.applicant.config.GradeCooldownProperties;
import com.maboy.applicantmarket.applicant.dao.ApplicantPrivacySettingsDao;
import com.maboy.applicantmarket.applicant.dao.ApplicantProfileDao;
import com.maboy.applicantmarket.applicant.dao.ApplicantSkillDao;
import com.maboy.applicantmarket.applicant.dao.FspAchievementStubDao;
import com.maboy.applicantmarket.applicant.dao.dto.ApplicantPrivacySettingsDto;
import com.maboy.applicantmarket.applicant.dao.dto.ApplicantProfileDto;
import com.maboy.applicantmarket.applicant.dao.dto.ApplicantSkillDto;
import com.maboy.applicantmarket.commons.dao.SpecializationDao;
import com.maboy.applicantmarket.commons.dao.dto.SpecializationDto;
import com.maboy.applicantmarket.commons.exception.ApplicantNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicantModuleApiImpl implements ApplicantModuleApi {

    private final ApplicantProfileDao profileDao;
    private final ApplicantSkillDao skillDao;
    private final ApplicantPrivacySettingsDao privacyDao;
    private final FspAchievementStubDao fspDao;
    private final SpecializationDao specializationDao;
    private final GradeCooldownProperties gradeCooldownProperties;

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
                .city(profile.getCity())
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

    @Override
    public UUID getApplicantIdByUserId(UUID userId) {
        return profileDao.findByUserId(userId)
            .map(ApplicantProfileDto::getId)
            .orElseThrow(() -> new ApplicantNotFoundException("Profile not found for user " + userId));
    }

    @Override
    public boolean canChangeGrade(UUID applicantId, UUID skillId) {
        return skillDao.findByApplicantIdAndSkillId(applicantId, skillId)
            .map(skill -> {
                if (skill.getLastGradeChangeAt() == null) return true;
                Duration cooldown = Duration.ofDays(gradeCooldownProperties.getCooldown().toDays());
                return Duration.between(skill.getLastGradeChangeAt(), Instant.now())
                           .compareTo(cooldown) >= 0;
            })
            .orElse(false); // навыка нет — менять грейд нечему
    }

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, ApplicantSummary> getSummaries(Collection<UUID> applicantIds) {
        if (applicantIds == null || applicantIds.isEmpty()) {
            return Map.of();
        }
        List<ApplicantProfileDto> profiles = profileDao.findAllById(applicantIds);

        // Один запрос за privacy
        Map<UUID, Boolean> visibleByApplicant = privacyDao
            .findAllByApplicantIdIn(applicantIds).stream()
            .collect(Collectors.toMap(
                ApplicantPrivacySettingsDto::getApplicantId,
                p -> Boolean.TRUE.equals(p.getVisibleInSearch())));

        // Один запрос за primary skills
        List<ApplicantSkillDto> primaries = skillDao
            .findAllByApplicantIdInAndIsPrimaryTrue(applicantIds);
        Map<UUID, ApplicantSkillDto> primaryByApplicant = primaries.stream()
            .collect(Collectors.toMap(ApplicantSkillDto::getApplicantId, s -> s));

        return profiles.stream().collect(Collectors.toMap(
            ApplicantProfileDto::getId,
            p -> {
                ApplicantSkillDto primary = primaryByApplicant.get(p.getId());
                return ApplicantSummary.builder()
                    .applicantId(p.getId())
                    .displayName(buildDisplayName(p))
                    .city(p.getCity())
                    .primarySkillId(primary != null ? primary.getSkillId() : null)
                    .primaryGradeId(primary != null ? primary.getVerifiedGradeId() : null)
                    .experienceYears(p.getExperienceYears())
                    .visibleInSearch(visibleByApplicant.getOrDefault(p.getId(), true))
                    .build();
            }
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, Set<UUID>> getVerifiedSkillIdsForAll(Collection<UUID> applicantIds) {
        if (applicantIds == null || applicantIds.isEmpty()) {
            return Map.of();
        }
        List<ApplicantSkillDto> skills = skillDao
            .findAllByApplicantIdInAndVerifiedGradeIdIsNotNull(applicantIds);

        Map<UUID, Set<UUID>> result = new HashMap<>();
        for (ApplicantSkillDto s : skills) {
            result.computeIfAbsent(s.getApplicantId(), k -> new HashSet<>())
                .add(s.getSkillId());
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UUID> getApplicantIdsByPrimarySkillCategoryAndGrade(UUID specializationId, UUID gradeId) {
        if (specializationId == null || gradeId == null) {
            return List.of();
        }
        // Найти специализацию по id, чтобы взять её code (== skills.category)
        SpecializationDto spec = specializationDao.findById(specializationId)
            .orElse(null);
        if (spec == null) return List.of();

        // Найти все активные skills с этим category
        List<UUID> skillIds = skillDao.findSkillsByCategory(spec.getCode());
        if (skillIds.isEmpty()) return List.of();

        // Все applicants, у которых primary-навык из этого списка и verified_grade_id = gradeId
        return skillDao.findApplicantIdsByPrimarySkillInAndVerifiedGrade(
            skillIds, gradeId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countApplicantsInCategory(UUID specializationId, UUID gradeId) {
        if (specializationId == null || gradeId == null) {
            return 0L;
        }
        // specializationId → code (skills.category == specialization.code)
        SpecializationDto spec = specializationDao.findById(specializationId)
            .orElse(null);
        if (spec == null) {
            return 0L;
        }
        return skillDao.countPrimaryWithGradeAndCategory(spec.getCode(), gradeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, Set<UUID>> getAllSkillIdsForAll(Collection<UUID> applicantIds) {
        if (applicantIds == null || applicantIds.isEmpty()) {
            return Map.of();
        }
        List<ApplicantSkillDto> skills = skillDao
            .findAllByApplicantIdIn(applicantIds);

        Map<UUID, Set<UUID>> result = new HashMap<>();
        for (ApplicantSkillDto s : skills) {
            result.computeIfAbsent(s.getApplicantId(), k -> new HashSet<>())
                .add(s.getSkillId());
        }
        return result;
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