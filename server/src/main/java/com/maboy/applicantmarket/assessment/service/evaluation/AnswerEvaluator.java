package com.maboy.applicantmarket.assessment.service.evaluation;

import java.util.Map;

public interface AnswerEvaluator {
    String type();
    boolean evaluate(Map<String, Object> correctAnswer, Map<String, Object> userAnswer);
}