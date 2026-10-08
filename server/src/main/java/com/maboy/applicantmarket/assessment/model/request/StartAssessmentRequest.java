package com.maboy.applicantmarket.assessment.model.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StartAssessmentRequest {
    private UUID skillId;
    private UUID claimedGradeId;
}