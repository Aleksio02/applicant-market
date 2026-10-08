package com.maboy.applicantmarket.commons.exception.assessment;

/**
 * Бросается при недопустимом переходе состояния сессии:
 * ответ на терминальную сессию, повторное завершение и т. п.
 */
public class AssessmentSessionStateException extends RuntimeException {
    public AssessmentSessionStateException(String message) { super(message); }
}