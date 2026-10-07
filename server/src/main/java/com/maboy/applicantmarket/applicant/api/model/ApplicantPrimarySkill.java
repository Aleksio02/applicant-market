package com.maboy.applicantmarket.applicant.api.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicantPrimarySkill {
    private UUID skillId;
    private UUID gradeId;
}