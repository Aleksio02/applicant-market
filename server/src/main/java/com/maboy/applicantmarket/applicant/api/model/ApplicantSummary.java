package com.maboy.applicantmarket.applicant.api.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicantSummary {
    private UUID applicantId;
    private String displayName;
    private UUID primarySkillId;
    private UUID primaryGradeId;
    private Short experienceYears;
    private boolean visibleInSearch;
}