package com.maboy.applicantmarket.commons.exception.assessment;

public class AssessmentNotReadyForCompletionException extends RuntimeException {
    public AssessmentNotReadyForCompletionException(int total, int answered) {
        super("Cannot complete session: " + answered + " of " + total + " items answered");
    }
}