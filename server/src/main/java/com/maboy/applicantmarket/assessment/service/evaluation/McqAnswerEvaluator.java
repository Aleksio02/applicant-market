package com.maboy.applicantmarket.assessment.service.evaluation;

import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class McqAnswerEvaluator implements AnswerEvaluator {

    @Override
    public String type() {
        return "MCQ";
    }

    @Override
    public boolean evaluate(Map<String, Object> correctAnswer, Map<String, Object> userAnswer) {
        Object correct = correctAnswer.get("selected");
        Object user = userAnswer.get("selected");
        if (correct == null || user == null) {
            return false;
        }
        return correct.toString().trim().equalsIgnoreCase(user.toString().trim());
    }
}