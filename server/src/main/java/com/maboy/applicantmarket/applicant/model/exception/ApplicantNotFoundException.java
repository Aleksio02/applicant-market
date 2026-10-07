package com.maboy.applicantmarket.applicant.model.exception;

public class ApplicantNotFoundException extends RuntimeException {
    public ApplicantNotFoundException(String message) { super(message); }
}