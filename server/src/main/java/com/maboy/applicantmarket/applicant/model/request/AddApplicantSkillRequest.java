package com.maboy.applicantmarket.applicant.model.request;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddApplicantSkillRequest {
    private UUID skillId;
    private Short selfAssessedLevel;
    private BigDecimal yearsExperience;
}