package com.maboy.applicantmarket.applicant.model.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantSkillResponse {
    private UUID id;
    private UUID skillId;
    private String skillCode;
    private String skillName;
    private String skillCategory;
    private Short selfAssessedLevel;
    private UUID verifiedGradeId;
    private Instant verifiedAt;
    private boolean primary;
    private BigDecimal yearsExperience;
}