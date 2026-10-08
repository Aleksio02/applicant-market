package com.maboy.applicantmarket.commons.exception.assessment;

import java.util.UUID;

/**
 * Не хватает активных шаблонов для генерации сессии.
 * Например, для difficulty=5 у навыка X нет ни одного шаблона.
 */
public class AssessmentTemplatePoolExhaustedException extends RuntimeException {
    public AssessmentTemplatePoolExhaustedException(UUID skillId, Short difficulty, int required, int available) {
        super("Not enough templates for skill " + skillId +
              " difficulty " + difficulty +
              ": required " + required + ", available " + available);
    }
}