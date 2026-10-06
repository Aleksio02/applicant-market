package com.maboy.applicantmarket.commons.exception;

public class IncorrectRequestDataException extends RuntimeException {
    public IncorrectRequestDataException(String message) {
        super(message);
    }
}