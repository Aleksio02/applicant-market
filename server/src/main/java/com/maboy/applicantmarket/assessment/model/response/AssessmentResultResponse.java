package com.maboy.applicantmarket.assessment.model.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentResultResponse {
    private UUID sessionId;
    private String status;
    private BigDecimal score;
    private UUID resultGradeId;
}