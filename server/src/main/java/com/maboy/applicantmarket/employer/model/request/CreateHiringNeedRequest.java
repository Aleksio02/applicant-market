package com.maboy.applicantmarket.employer.model.request;

import com.maboy.applicantmarket.commons.model.enums.WorkFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Нижняя граница зарплаты, целое число в рублях",
            example = "200000")
    @NotNull(message = "Salary from must not be null")
    @Positive(message = "Salary from must be positive")
    private Long salaryFrom;

    @Schema(description = "Верхняя граница зарплаты, целое число в рублях",
            example = "300000")
    @NotNull(message = "Salary to must not be null")
    @Positive(message = "Salary to must be positive")
    private Long salaryTo;

    @Schema(description = "Формат работы: OFFICE — офис, REMOTE — удалённо, HYBRID — гибрид",
            example = "REMOTE")
    @NotNull(message = "Format must not be null")
    private WorkFormat format;

    private String location;
}