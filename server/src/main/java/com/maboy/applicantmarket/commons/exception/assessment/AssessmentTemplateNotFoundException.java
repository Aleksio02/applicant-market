package com.maboy.applicantmarket.commons.exception.assessment;

public class AssessmentTemplateNotFoundException extends RuntimeException {
    public AssessmentTemplateNotFoundException(String message) { super(message); }

    public static AssessmentTemplateNotFoundException byCode(String code) {
        return new AssessmentTemplateNotFoundException("Assessment template not found: " + code);
    }
}