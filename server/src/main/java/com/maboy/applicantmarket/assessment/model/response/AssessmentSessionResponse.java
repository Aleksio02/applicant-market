package com.maboy.applicantmarket.assessment.model.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentSessionResponse {
    private UUID id;
    private UUID skillId;
    private UUID claimedGradeId;
    private String status;
    private UUID resultGradeId;
    private BigDecimal score;
    private Instant startedAt;
    private Instant expiresAt;
    private Instant completedAt;
    private List<AssessmentItemResponse> items;
}