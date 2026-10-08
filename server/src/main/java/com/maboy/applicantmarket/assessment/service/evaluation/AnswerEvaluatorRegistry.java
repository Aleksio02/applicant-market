package com.maboy.applicantmarket.assessment.service.evaluation;

import com.maboy.applicantmarket.assessment.model.exception.AnswerEvaluationException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AnswerEvaluatorRegistry {

    private final Map<String, AnswerEvaluator> evaluators = new HashMap<>();

    public AnswerEvaluatorRegistry(List<AnswerEvaluator> all) {
        for (AnswerEvaluator e : all) {
            AnswerEvaluator existing = evaluators.put(e.type(), e);
            if (existing != null) {
                throw new IllegalStateException(
                        "Duplicate AnswerEvaluator for type: " + e.type());
            }
        }
    }

    public AnswerEvaluator get(String type) {
        AnswerEvaluator e = evaluators.get(type);
        if (e == null) {
            throw new AnswerEvaluationException("No evaluator for item type: " + type);
        }
        return e;
    }
}