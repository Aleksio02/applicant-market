package com.maboy.applicantmarket.applicant.model.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddExperienceRequest {
    private String company;
    private String position;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean current;
    private String description;
    private Integer sortOrder;
}