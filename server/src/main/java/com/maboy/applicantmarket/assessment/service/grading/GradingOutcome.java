package com.maboy.applicantmarket.assessment.service.grading;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Результат оценки сессии.
 * <p>
 * status        — COMPLETED или FAILED resultGradeId — присвоенный грейд (null при FAILED) score         — achieved /
 * total, 4 знака после запятой
 */
public record GradingOutcome(
    String status,
    UUID resultGradeId,
    BigDecimal score
) {
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }
}