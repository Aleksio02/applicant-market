package com.maboy.applicantmarket.applicant.api.event;

import java.util.UUID;

public record AssessmentCompleted(
        UUID applicantId,
        UUID skillId,
        UUID gradeId,
        double score
) {}