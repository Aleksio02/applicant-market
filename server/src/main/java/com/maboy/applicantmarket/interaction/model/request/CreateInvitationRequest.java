package com.maboy.applicantmarket.interaction.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Schema(description = "Запрос на создание приглашения кандидату от работодателя")
public class CreateInvitationRequest {

    @Schema(description = "ID кандидата, которому направляется приглашение",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Candidate must not be null")
    private UUID candidateId;

    @Schema(description = "ID вакансии. Опционален: можно пригласить кандидата без привязки к вакансии. " +
            "Если передан, зарплата берётся из вакансии, а salaryFrom/salaryTo в теле игнорируются.")
    private UUID vacancyId;

    @Schema(description = "Заголовок карточки приглашения. Используется для приглашений без вакансии. " +
            "Если vacancyId передан, заголовок берётся из вакансии.",
            example = "Senior Backend в финтех")
    @Size(max = 255)
    private String title;

    @Schema(description = "Текст предложения от работодателя",
            example = "Есть интересная роль, хотим познакомиться")
    private String message;

    @Schema(description = "Нижняя граница зарплаты, целое число в рублях. " +
            "Обязательна, если vacancyId не передан.",
            example = "350000")
    private Long salaryFrom;

    @Schema(description = "Верхняя граница зарплаты, целое число в рублях. " +
            "Обязательна, если vacancyId не передан.",
            example = "450000")
    private Long salaryTo;
}