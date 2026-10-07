package com.maboy.applicantmarket.employer.model.request;

import com.maboy.applicantmarket.employer.model.enums.WorkFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateHiringNeedRequest {

    @NotBlank(message = "Title must not be empty")
    @Size(max = 255)
    private String title;

    private String description;

    @NotNull(message = "Specialization must not be null")
    private UUID specializationId;

    @NotNull(message = "Grade must not be null")
    private UUID gradeId;

    @NotNull(message = "Salary from must not be null")
    @Positive(message = "Salary from must be positive")
    private Long salaryFrom;

    @NotNull(message = "Salary to must not be null")
    @Positive(message = "Salary to must be positive")
    private Long salaryTo;

    @NotNull(message = "Format must not be null")
    private WorkFormat format;

    private String location;
}