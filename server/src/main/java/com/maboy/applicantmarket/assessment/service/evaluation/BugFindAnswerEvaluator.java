package com.maboy.applicantmarket.assessment.service.evaluation;

import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class BugFindAnswerEvaluator implements AnswerEvaluator {

    @Override
    public String type() {
        return "BUG_FIND";
    }

    @Override
    public boolean evaluate(Map<String, Object> correctAnswer, Map<String, Object> userAnswer) {
        Long correct = toLong(correctAnswer.get("value"));
        Long user = toLong(userAnswer.get("value"));
        if (correct == null || user == null) {
            return false;
        }
        return correct.equals(user);
    }

    private Long toLong(Object v) {
        if (v == null) {
            return null;
        }
        if (v instanceof Number n) {
            return n.longValue();
        }
        if (v instanceof String s) {
            try {
                return Long.parseLong(s.trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}