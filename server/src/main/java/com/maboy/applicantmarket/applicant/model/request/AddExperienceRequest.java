package com.maboy.applicantmarket.applicant.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на добавление опыта работы")
public class AddExperienceRequest {

    @Schema(description = "Название компании", example = "Acme Corp")
    private String company;

    @Schema(description = "Должность", example = "Backend-разработчик")
    private String position;

    @Schema(description = "Дата начала работы", example = "2020-03-01")
    private LocalDate startDate;

    @Schema(description = "Дата окончания работы. Оставьте пустым, если работаете здесь сейчас.",
            example = "2023-06-30")
    private LocalDate endDate;

    @Schema(description = "true, если это текущее место работы", example = "false")
    private Boolean current;

    @Schema(description = "Описание обязанностей и достижений",
            example = "Разрабатывал платёжный сервис на Java и Spring Boot")
    private String description;

    @Schema(description = "Порядок отображения в списке", example = "0")
    private Integer sortOrder;
}