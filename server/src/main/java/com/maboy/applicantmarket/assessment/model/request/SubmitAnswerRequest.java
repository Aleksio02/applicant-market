package com.maboy.applicantmarket.assessment.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ на задание тестирования")
public class SubmitAnswerRequest {

    @Schema(description = "ID задания, на которое отвечает кандидат",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Item must not be null")
    private UUID itemId;

    @Schema(description = """
            Тело ответа. Формат зависит от типа задания:
            - MCQ: {"selected": "A"} — буква выбранного варианта
            - OUTPUT_PREDICT: {"value": 42} — предсказанное значение
            - BUG_FIND: {"value": 3} — номер строки с ошибкой
            """,
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Answer must not be null")
    private Map<String, Object> answer;
}