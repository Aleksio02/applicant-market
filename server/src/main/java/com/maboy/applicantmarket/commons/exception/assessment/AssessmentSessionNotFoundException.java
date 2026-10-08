package com.maboy.applicantmarket.commons.exception.assessment;

public class AssessmentSessionNotFoundException extends RuntimeException {
    public AssessmentSessionNotFoundException(String message) { super(message); }

    public static AssessmentSessionNotFoundException byId(Object id) {
        return new AssessmentSessionNotFoundException("Assessment session not found: " + id);
    }
}