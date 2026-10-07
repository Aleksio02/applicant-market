package com.maboy.applicantmarket.applicant.model.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateApplicantProfileRequest {
    private String firstName;
    private String lastName;
    private String middleName;
    private String phone;
    private String city;
    private String country;
    private String about;
    private Short experienceYears;
}