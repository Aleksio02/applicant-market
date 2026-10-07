package com.maboy.applicantmarket.applicant.model.response;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantExperienceResponse {
    private UUID id;
    private String company;
    private String position;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean current;
    private String description;
    private Integer sortOrder;
}