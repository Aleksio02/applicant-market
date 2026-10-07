package com.maboy.applicantmarket.applicant.model.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantProfileCompletenessResponse {
    private int percent;
    private List<String> missing;
}