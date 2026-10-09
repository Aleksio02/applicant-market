package com.maboy.applicantmarket.assignment.model.request;

import com.maboy.applicantmarket.assignment.model.enums.EvaluationVerdict;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Запрос на оценку попытки прохождения тестового задания")
public class EvaluateAttemptRequest {

    @Schema(description = "Вердикт: PASS — задание принято (создаётся отклик), FAIL — отклонено",
            example = "PASS",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Verdict must not be null")
    private EvaluationVerdict verdict;

    @Schema(description = "Оценка в баллах (опционально)",
            example = "90")
    private Integer score;

    @Schema(description = "Комментарий к оценке (опционально)",
            example = "Отличное решение, но можно было добавить пагинацию")
    private String feedback;
}