package com.maboy.applicantmarket.applicant.converter;

import com.maboy.applicantmarket.applicant.dao.dto.ApplicantSkillDto;
import com.maboy.applicantmarket.applicant.model.ApplicantSkill;
import com.maboy.applicantmarket.applicant.model.response.ApplicantSkillResponse;
import com.maboy.applicantmarket.commons.dao.SkillDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicantSkillConverter {

    private final SkillDao skillDao;

    public ApplicantSkill fromDto(ApplicantSkillDto e) {
        if (e == null) return null;
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

    public ApplicantSkillDto toDto(ApplicantSkill m) {
        if (m == null) return null;
        return ApplicantSkillDto.builder()
                .id(m.getId())
                .applicantId(m.getApplicantId())
                .skillId(m.getSkillId())
                .selfAssessedLevel(m.getSelfAssessedLevel())
                .verifiedGradeId(m.getVerifiedGradeId())
                .lastGradeChangeAt(m.getLastGradeChangeAt())
                .verifiedAt(m.getVerifiedAt())
                .isPrimary(m.isPrimary())
                .yearsExperience(m.getYearsExperience())
                .build();
    }

    public ApplicantSkillResponse toResponse(ApplicantSkill m) {
        if (m == null) return null;
        ApplicantSkillResponse r = new ApplicantSkillResponse();
        r.setId(m.getId());
        r.setSkillId(m.getSkillId());
        r.setSelfAssessedLevel(m.getSelfAssessedLevel());
        r.setVerifiedGradeId(m.getVerifiedGradeId());
        r.setVerifiedAt(m.getVerifiedAt());
        r.setPrimary(m.isPrimary());
        r.setYearsExperience(m.getYearsExperience());

        skillDao.findById(m.getSkillId()).ifPresent(s -> {
            r.setSkillCode(s.getCode());
            r.setSkillName(s.getName());
            r.setSkillCategory(s.getCategory());
        });
        return r;
    }
}