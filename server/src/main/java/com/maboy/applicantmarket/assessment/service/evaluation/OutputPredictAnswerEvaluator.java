package com.maboy.applicantmarket.assessment.service.evaluation;

import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class OutputPredictAnswerEvaluator implements AnswerEvaluator {

    @Override
    public String type() {
        return "OUTPUT_PREDICT";
    }

    @Override
    public boolean evaluate(Map<String, Object> correctAnswer, Map<String, Object> userAnswer) {
        Object correct = correctAnswer.get("value");
        Object user = userAnswer.get("value");
        if (correct == null || user == null) {
            return false;
        }
        return normalize(correct).equals(normalize(user));
    }

    /**
     * Приводим к сопоставимому виду: - null → пустая строка (уже отсекли выше, но на всякий случай); - строка → trim,
     * без внутренних пробелов, нижний регистр; - число → строка без хвостовых нулей (7.0 и 7 — одно и то же); - boolean
     * → нижний регистр.
     */
    private String normalize(Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof Number n) {
            // Целые числа и long → без десятичной части.
            if (value instanceof Long || value instanceof Integer || value instanceof Short) {
                return String.valueOf(n.longValue());
            }
            double d = n.doubleValue();
            if (d == Math.rint(d) && !Double.isInfinite(d)) {
                return String.valueOf((long) d);
            }
            return String.valueOf(d);
        }

        String s = value.toString().trim().toLowerCase();
        return s.replaceAll("\\s+", "");
    }
}