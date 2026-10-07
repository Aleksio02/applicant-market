package com.maboy.applicantmarket.applicant.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicantSkill {
    private UUID id;
    private UUID applicantId;
    private UUID skillId;
    private Short selfAssessedLevel;
    private UUID verifiedGradeId;
    private Instant lastGradeChangeAt;
    private Instant verifiedAt;
    private boolean primary;
    private BigDecimal yearsExperience;

    public boolean canChangeGrade(Duration cooldown, Instant now) {
        if (lastGradeChangeAt == null) return true;
        return Duration.between(lastGradeChangeAt, now).compareTo(cooldown) >= 0;
    }

    public void verifyGrade(UUID gradeId, Instant now) {
        this.verifiedGradeId = gradeId;
        this.verifiedAt = now;
        this.lastGradeChangeAt = now;
    }

    public void markPrimary() {
        this.primary = true;
    }

    public void unmarkPrimary() {
        this.primary = false;
    }
}