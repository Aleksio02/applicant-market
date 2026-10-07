package com.maboy.applicantmarket.employer.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCompanyRequest {

    @NotBlank(message = "Company name must not be empty")
    @Size(max = 255, message = "Company name is too long")
    private String name;

    private String description;
    private String industry;
    private String website;
    private String contactPersonName;
    private String contactPersonPosition;
    private String contactEmail;
    private String contactPhone;
    private String logoUrl;
}