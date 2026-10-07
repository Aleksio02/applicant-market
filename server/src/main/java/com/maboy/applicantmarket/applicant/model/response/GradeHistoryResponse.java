package com.maboy.applicantmarket.applicant.model.response;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GradeHistoryResponse {
    private UUID id;
    private UUID skillId;
    private UUID fromGradeId;
    private UUID toGradeId;
    private String reason;
    private Instant changedAt;
}