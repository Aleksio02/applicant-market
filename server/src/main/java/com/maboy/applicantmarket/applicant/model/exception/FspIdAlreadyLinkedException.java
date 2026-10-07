package com.maboy.applicantmarket.applicant.model.exception;

public class FspIdAlreadyLinkedException extends RuntimeException {
    public FspIdAlreadyLinkedException(String message) { super(message); }
}