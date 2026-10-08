package com.maboy.applicantmarket.assignment.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class VacancyAssignment {
    private UUID id;
    private UUID vacancyId;
    private String title;
    private String description;
    private Integer durationHours;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}