package com.maboy.applicantmarket.applicant.model;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeChangeHistory {
    private UUID id;
    private UUID applicantId;
    private UUID skillId;
    private UUID fromGradeId;
    private UUID toGradeId;
    private String reason;
    private Instant changedAt;
}