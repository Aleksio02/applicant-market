package com.maboy.applicantmarket.applicant.api.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicantSkillRef {
    private UUID skillId;
    private UUID gradeId;
    private boolean primary;
}