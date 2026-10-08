package com.maboy.applicantmarket.assessment.model.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentHistoryItemResponse {
    private UUID sessionId;
    private UUID skillId;
    private UUID claimedGradeId;
    private UUID resultGradeId;
    private String status;
    private BigDecimal score;
    private Instant startedAt;
    private Instant completedAt;
}