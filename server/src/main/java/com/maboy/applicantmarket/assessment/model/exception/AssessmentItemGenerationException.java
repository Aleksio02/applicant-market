package com.maboy.applicantmarket.assessment.model.exception;

import java.util.UUID;

/**
 * Одна попытка генерации item'а не удалась.
 * AssessmentSessionGenerator ловит её и пробует другой шаблон.
 */
public class AssessmentItemGenerationException extends RuntimeException {
    private final UUID templateId;
    private final String templateCode;

    public AssessmentItemGenerationException(UUID templateId, String templateCode, String reason) {
        super("Failed to generate item from template [" + templateCode + "]: " + reason);
        this.templateId = templateId;
        this.templateCode = templateCode;
    }

    public AssessmentItemGenerationException(UUID templateId, String templateCode,
                                             String reason, Throwable cause) {
        super("Failed to generate item from template [" + templateCode + "]: " + reason, cause);
        this.templateId = templateId;
        this.templateCode = templateCode;
    }

    public UUID getTemplateId() {
        return templateId;
    }

    public String getTemplateCode() {
        return templateCode;
    }
}