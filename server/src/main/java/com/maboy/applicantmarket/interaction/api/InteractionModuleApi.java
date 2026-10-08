package com.maboy.applicantmarket.interaction.api;

import com.maboy.applicantmarket.interaction.api.model.InteractionRef;

import java.util.Optional;
import java.util.UUID;

public interface InteractionModuleApi {

    /**
     * Создать отклик от кандидата по итогам успешного прохождения тестового задания.
     * Вызывается из assignment при verdict = PASS.
     * Идемпотентно: если активный отклик уже есть, повторно не создаётся.
     */
    Optional<InteractionRef> createApplicationFromAssignment(UUID candidateId, UUID vacancyId);

    /**
     * Проверить, есть ли активное взаимодействие кандидата и вакансии.
     */
    boolean hasActiveInteraction(UUID candidateId, UUID vacancyId);
}