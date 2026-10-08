package com.maboy.applicantmarket.assessment.model.exception;

public class AnswerEvaluationException extends RuntimeException {
    public AnswerEvaluationException(String message) {
        super(message);
    }

    public AnswerEvaluationException(String message, Throwable cause) {
        super(message, cause);
    }
}