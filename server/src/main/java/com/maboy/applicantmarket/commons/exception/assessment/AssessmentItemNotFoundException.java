package com.maboy.applicantmarket.commons.exception.assessment;

public class AssessmentItemNotFoundException extends RuntimeException {
    public AssessmentItemNotFoundException(String message) { super(message); }

    public static AssessmentItemNotFoundException byId(Object id) {
        return new AssessmentItemNotFoundException("Assessment item not found: " + id);
    }
}