package com.maboy.applicantmarket.applicant.model.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateApplicantSkillRequest {
    private Short selfAssessedLevel;
    private BigDecimal yearsExperience;
}