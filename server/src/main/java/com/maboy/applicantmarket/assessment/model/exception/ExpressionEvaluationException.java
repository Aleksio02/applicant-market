package com.maboy.applicantmarket.assessment.model.exception;

public class ExpressionEvaluationException extends RuntimeException {
    public ExpressionEvaluationException(String message) {
        super(message);
    }

    public ExpressionEvaluationException(String message, Throwable cause) {
        super(message, cause);
    }
}