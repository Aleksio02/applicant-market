package com.maboy.applicantmarket.assignment.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAssignmentRequest {

    @NotBlank(message = "Title must not be empty")
    @Size(max = 255)
    private String title;

    @NotBlank(message = "Description must not be empty")
    private String description;

    @NotNull(message = "Duration hours must not be null")
    @Min(value = 1, message = "Duration hours must be at least 1")
    private Integer durationHours;
}