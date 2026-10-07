package com.maboy.applicantmarket.employer.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class Company {
    private UUID id;
    private UUID ownerId;
    private String name;
    private String description;
    private String industry;
    private String website;
    private String contactPersonName;
    private String contactPersonPosition;
    private String contactEmail;
    private String contactPhone;
    private String logoUrl;
    private Instant createdAt;
    private Instant updatedAt;
}