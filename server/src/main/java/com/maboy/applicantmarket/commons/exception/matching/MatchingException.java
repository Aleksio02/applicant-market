package com.maboy.applicantmarket.commons.exception.matching;

public class MatchingException extends RuntimeException {
    public MatchingException(String message) { super(message); }
    public MatchingException(String message, Throwable cause) { super(message, cause); }
}