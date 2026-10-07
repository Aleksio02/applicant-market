package com.maboy.applicantmarket.applicant.api;

import com.maboy.applicantmarket.applicant.api.model.ApplicantPrimarySkill;
import com.maboy.applicantmarket.applicant.api.model.ApplicantSkillRef;
import com.maboy.applicantmarket.applicant.api.model.ApplicantSummary;
import com.maboy.applicantmarket.applicant.api.model.FspAchievementRef;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicantModuleApi {
    ApplicantSummary getSummary(UUID applicantId);
    List<ApplicantSkillRef> getVerifiedSkills(UUID applicantId);
    Optional<ApplicantPrimarySkill> getPrimarySkill(UUID applicantId);
    boolean hasFspHistory(UUID applicantId);
    List<FspAchievementRef> getFspAchievements(UUID applicantId);
    List<UUID> findApplicantIdsBySkillAndGrade(UUID skillId, UUID gradeId);
}