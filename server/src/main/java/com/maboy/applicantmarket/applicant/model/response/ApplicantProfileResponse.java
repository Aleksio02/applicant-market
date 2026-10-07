package com.maboy.applicantmarket.applicant.model.response;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantProfileResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String phone;
    private String city;
    private String country;
    private String about;
    private Short experienceYears;
    private String fspId;
    private Instant fspLinkedAt;
    private String status;
}