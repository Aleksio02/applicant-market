package com.maboy.applicantmarket.commons.exception.assessment;

import java.util.UUID;

public class AssessmentSessionAlreadyActiveException extends RuntimeException {
    public AssessmentSessionAlreadyActiveException(UUID sessionId) {
        super("An active assessment session already exists: " + sessionId);
    }
}