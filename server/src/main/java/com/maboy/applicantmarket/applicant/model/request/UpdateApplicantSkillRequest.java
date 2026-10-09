package com.maboy.applicantmarket.applicant.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на обновление навыка. Пустые поля не изменяются.")
public class UpdateApplicantSkillRequest {

    @Schema(description = "Самооценка уровня владения по шкале от 1 до 5",
            example = "4", minimum = "1", maximum = "5")
    private Short selfAssessedLevel;

    @Schema(description = "Опыт работы с навыком в годах",
            example = "3.5")
    private BigDecimal yearsExperience;
}