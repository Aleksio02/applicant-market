package com.maboy.applicantmarket.commons.exception.assessment;

import java.time.Instant;
import java.util.UUID;

public class AssessmentRetryCooldownException extends RuntimeException {
    public AssessmentRetryCooldownException(UUID skillId, Instant lastAttemptAt) {
        super("Assessment retry cooldown active for skill " + skillId +
              " (last attempt: " + lastAttemptAt + ")");
    }
}