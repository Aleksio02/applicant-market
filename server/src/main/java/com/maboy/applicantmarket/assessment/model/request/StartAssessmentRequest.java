package com.maboy.applicantmarket.assessment.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на старт сессии тестирования")
public class StartAssessmentRequest {

    @Schema(description = "ID навыка из справочника, по которому проходит тестирование",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Skill must not be null")
    private UUID skillId;

    @Schema(description = "ID грейда, на который претендует кандидат",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Claimed grade must not be null")
    private UUID claimedGradeId;
}