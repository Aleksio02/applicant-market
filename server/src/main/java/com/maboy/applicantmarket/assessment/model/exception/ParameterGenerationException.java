package com.maboy.applicantmarket.assessment.model.exception;

public class ParameterGenerationException extends RuntimeException {
    public ParameterGenerationException(String message) {
        super(message);
    }

    public ParameterGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}