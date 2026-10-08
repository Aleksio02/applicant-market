package com.maboy.applicantmarket.assessment.model;

import com.maboy.applicantmarket.commons.exception.assessment.AssessmentSessionStateException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
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
public class AssessmentSession {

    private static final Set<String> TERMINAL_STATUSES =
        Set.of("COMPLETED", "FAILED", "EXPIRED", "CANCELLED");

    private UUID id;
    private UUID applicantId;
    private UUID skillId;
    private UUID claimedGradeId;
    private String status;
    private UUID resultGradeId;
    private BigDecimal score;
    private Instant startedAt;
    private Instant completedAt;
    private Instant expiresAt;

    public boolean isActive() {
        return !TERMINAL_STATUSES.contains(status);
    }

    public boolean isExpired(Instant now) {
        return isActive() && now.isAfter(expiresAt);
    }

    /**
     * Проверка перед ответом или завершением. Используется в сервисе для ленивого закрытия истёкших сессий.
     */
    public void ensureActive(Instant now) {
        if (isExpired(now)) {
            throw new AssessmentSessionStateException("Session expired: " + id);
        }
        if (!isActive()) {
            throw new AssessmentSessionStateException("Session is not active: " + status);
        }
    }

    public void markExpired(Instant now) {
        if (!isActive()) {
            return;
        }
        this.status = "EXPIRED";
        this.completedAt = now;
    }

    public void complete(UUID resultGradeId, BigDecimal score, Instant now) {
        ensureActive(now);
        this.status = "COMPLETED";
        this.resultGradeId = resultGradeId;
        this.score = score;
        this.completedAt = now;
    }

    public void fail(BigDecimal score, Instant now) {
        ensureActive(now);
        this.status = "FAILED";
        this.score = score;
        this.completedAt = now;
    }
}