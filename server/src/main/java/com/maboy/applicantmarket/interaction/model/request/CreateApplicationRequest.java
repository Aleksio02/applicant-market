package com.maboy.applicantmarket.interaction.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Запрос на отклик кандидата на вакансию")
public class CreateApplicationRequest {

    @Schema(description = "Комментарий кандидата к отклику (опционально)",
            example = "Хочу присоединиться к вашей команде")
    private String message;
}