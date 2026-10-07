package com.maboy.applicantmarket.vacancy.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateVacancyRequirementRequest {

    @NotNull(message = "Skill must not be null")
    private UUID skillId;

    @Min(value = 1, message = "Level must be between 1 and 5")
    @Max(value = 5, message = "Level must be between 1 and 5")
    private short level;

    private boolean mandatory;
}