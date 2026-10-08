package com.maboy.applicantmarket.commons.exception.assessment;

import java.util.UUID;

/**
 * Cooldown на смену грейда по навыку ещё не истёк.
 * Проверяется на старте сессии через ApplicantModuleApi.canChangeGrade.
 */
public class AssessmentCooldownException extends RuntimeException {
    public AssessmentCooldownException(UUID skillId) {
        super("Grade change cooldown has not elapsed for skill: " + skillId);
    }
}