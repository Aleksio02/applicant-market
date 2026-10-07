package com.maboy.applicantmarket.applicant.model.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantEducationResponse {
    private UUID id;
    private String institution;
    private String degree;
    private String field;
    private Short startYear;
    private Short endYear;
}