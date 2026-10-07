package com.maboy.applicantmarket.employer.model;

import com.maboy.applicantmarket.commons.model.Grade;
import com.maboy.applicantmarket.commons.model.Specialization;
import com.maboy.applicantmarket.employer.model.enums.WorkFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class HiringNeed {
    private UUID id;
    private UUID companyId;
    private String title;
    private String description;
    private UUID specializationId;
    private UUID gradeId;
    private Specialization specialization;
    private Grade grade;
    private Long salaryFrom;
    private Long salaryTo;
    private WorkFormat format;
    private String location;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}