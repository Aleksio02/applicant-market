package com.maboy.applicantmarket.employer.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Направление деятельности компании, например: IT, финтех, e-commerce",
            example = "IT")
    private String industry;

    private String website;

    @Schema(description = "ФИО представителя компании, а не название компании",
            example = "Иван Иванов")
    private String contactPersonName;

    @Schema(description = "Должность представителя компании",
            example = "HR Manager")
    private String contactPersonPosition;

    private String contactEmail;
    private String contactPhone;
    private String logoUrl;
}