package com.maboy.applicantmarket.commons.exception.assessment;

import java.util.UUID;

public class AssessmentAnswerAlreadyExistsException extends RuntimeException {
    public AssessmentAnswerAlreadyExistsException(UUID itemId) {
        super("Answer already exists for item: " + itemId);
    }
}