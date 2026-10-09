package com.maboy.applicantmarket.vacancy.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Требуемый уровень владения навыком по шкале от 1 до 5",
            example = "4",
            minimum = "1", maximum = "5")
    @Min(value = 1, message = "Level must be between 1 and 5")
    @Max(value = 5, message = "Level must be between 1 and 5")
    private short level;

    @Schema(description = "true — обязательный навык, false — желательный",
            example = "true")
    private boolean mandatory;
}