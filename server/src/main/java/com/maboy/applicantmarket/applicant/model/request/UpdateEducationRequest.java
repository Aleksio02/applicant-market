package com.maboy.applicantmarket.applicant.model.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEducationRequest {
    private String institution;
    private String degree;
    private String field;
    private Short startYear;
    private Short endYear;
}