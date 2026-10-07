package com.maboy.applicantmarket.applicant.model;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicantEducation {
    private UUID id;
    private UUID applicantId;
    private String institution;
    private String degree;
    private String field;
    private Short startYear;
    private Short endYear;
    private Instant createdAt;
    private Instant updatedAt;
}