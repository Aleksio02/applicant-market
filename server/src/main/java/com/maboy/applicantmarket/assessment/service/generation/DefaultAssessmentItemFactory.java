package com.maboy.applicantmarket.assessment.service.generation;

import com.maboy.applicantmarket.assessment.config.AssessmentProperties;
import com.maboy.applicantmarket.assessment.model.AssessmentItem;
import com.maboy.applicantmarket.assessment.model.AssessmentTemplate;
import com.maboy.applicantmarket.assessment.model.exception.AssessmentItemGenerationException;
import com.maboy.applicantmarket.assessment.service.expression.ExpressionEvaluator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultAssessmentItemFactory implements AssessmentItemFactory {

    private static final List<String> OPTION_IDS = List.of("A", "B", "C", "D", "E", "F");

    private final ParameterGenerator parameterGenerator;
    private final ExpressionEvaluator expressionEvaluator;
    private final TemplateRenderer templateRenderer;
    private final AssessmentProperties properties;

    @Override
    public AssessmentItem generate(AssessmentTemplate template, int position, long itemSeed) {
        Random rnd = new Random(itemSeed);

        int attempts = properties.getGeneration().getMaxAttemptsPerTemplate();
        AssessmentItemGenerationException last = null;

        for (int attempt = 1; attempt <= attempts; attempt++) {
            try {
                return tryGenerate(template, position, itemSeed, rnd);
            } catch (AssessmentItemGenerationException e) {
                last = e;
                // не сбрасываем rnd — следующая попытка получит новые параметры
            }
        }
        throw last != null ? last
            : new AssessmentItemGenerationException(template.getId(), template.getCode(),
                "unknown reason"
            );
    }

    // ============================================================
    // Одна попытка генерации
    // ============================================================
    private AssessmentItem tryGenerate(AssessmentTemplate template, int position, long itemSeed, Random rnd) {
        Map<String, Object> parameters;
        try {
            parameters = parameterGenerator.generate(template.getParameterSpec(), rnd);
        } catch (RuntimeException e) {
            throw new AssessmentItemGenerationException(
                template.getId(), template.getCode(),
                "parameter generation failed: " + e.getMessage(), e
            );
        }

        String type = template.getType();
        Map<String, Object> correctAnswer;
        Map<String, String> extras = new LinkedHashMap<>();
        List<String> optionOrder = List.of();

        try {
            switch (type) {
                case "MCQ" -> {
                    McqResult mcq = buildMcq(template, parameters, rnd);
                    correctAnswer = mcq.correctAnswer();
                    extras.put("options", mcq.renderedOptions());
                    optionOrder = mcq.optionOrder();
                }
                case "OUTPUT_PREDICT", "BUG_FIND" -> {
                    Object value = evaluateAnswer(template, parameters);
                    correctAnswer = Map.of("value", value);
                }
                default -> throw new AssessmentItemGenerationException(
                    template.getId(), template.getCode(),
                    "unsupported type: " + type
                );
            }
        } catch (AssessmentItemGenerationException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new AssessmentItemGenerationException(
                template.getId(), template.getCode(),
                "answer evaluation failed: " + e.getMessage(), e
            );
        }

        String body;
        try {
            body = templateRenderer.renderBody(template.getBodyTemplate(), parameters, extras);
        } catch (RuntimeException e) {
            throw new AssessmentItemGenerationException(
                template.getId(), template.getCode(),
                "body rendering failed: " + e.getMessage(), e
            );
        }

        Map<String, Object> generationMeta = new LinkedHashMap<>();
        generationMeta.put("templateCode", template.getCode());
        generationMeta.put("seed", itemSeed);
        generationMeta.put("rendererVersion", properties.getGeneration().getRendererVersion());
        generationMeta.put("parameters", parameters);
        if (!optionOrder.isEmpty()) {
            generationMeta.put("optionOrder", optionOrder);
        }
        generationMeta.put("policySnapshot", Map.of(
            "totalItems", properties.getGeneration().getTotalItems()
        ));

        return AssessmentItem.builder()
            .templateId(template.getId())
            .templateCode(template.getCode())
            .position(position)
            .type(type)
            .topic(template.getTopic())
            .difficulty(template.getDifficulty())
            .body(body)
            .correctAnswer(correctAnswer)
            .parameters(parameters)
            .generationMeta(generationMeta)
            .points(template.getDifficulty())
            .build();
    }

    // ============================================================
    // MCQ
    // ============================================================
    private McqResult buildMcq(AssessmentTemplate template, Map<String, Object> parameters, Random rnd) {
        Map<String, Object> answerSpec = template.getAnswerSpec();
        Object correctExprObj = answerSpec.get("correct_expr");
        if (!(correctExprObj instanceof String correctExpr)) {
            throw new AssessmentItemGenerationException(
                template.getId(), template.getCode(),
                "MCQ requires 'correct_expr' string in answer_spec"
            );
        }
        Object distractorsObj = answerSpec.get("distractor_exprs");
        if (!(distractorsObj instanceof List<?> distractorList) || distractorList.isEmpty()) {
            throw new AssessmentItemGenerationException(
                template.getId(), template.getCode(),
                "MCQ requires non-empty 'distractor_exprs' list"
            );
        }

        Object correctValue = expressionEvaluator.evaluate(correctExpr, parameters);

        List<Object> distractorValues = new ArrayList<>(distractorList.size());
        for (Object expr : distractorList) {
            if (!(expr instanceof String s)) {
                throw new AssessmentItemGenerationException(
                    template.getId(), template.getCode(),
                    "MCQ distractor must be a string expression, got " + expr
                );
            }
            Object value = expressionEvaluator.evaluate(s, parameters);
            distractorValues.add(value);
        }

        // Проверка коллизий: ни один дистрактор не равен правильному
        // и все дистракторы между собой разные.
        for (Object d : distractorValues) {
            if (Objects.equals(d, correctValue)) {
                throw new AssessmentItemGenerationException(
                    template.getId(), template.getCode(),
                    "distractor collides with correct answer: " + d
                );
            }
        }
        Set<Object> unique = new HashSet<>(distractorValues);
        if (unique.size() != distractorValues.size()) {
            throw new AssessmentItemGenerationException(
                template.getId(), template.getCode(),
                "duplicate distractors: " + distractorValues
            );
        }

        // Собираем все варианты и перемешиваем детерминированно
        List<Object> allValues = new ArrayList<>();
        allValues.add(correctValue);
        allValues.addAll(distractorValues);

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < allValues.size(); i++) {
            indices.add(i);
        }
        Collections.shuffle(indices, rnd);

        List<String> optionOrder = new ArrayList<>(allValues.size());
        String optionsFormat = answerSpec.get("options_format") instanceof String s
            ? s
            : "{id}) {value}";

        StringBuilder renderedOptions = new StringBuilder();
        String correctId = null;

        for (int displayIdx = 0; displayIdx < indices.size(); displayIdx++) {
            int originalIdx = indices.get(displayIdx);
            Object value = allValues.get(originalIdx);
            String id = OPTION_IDS.get(displayIdx);
            optionOrder.add(id);

            if (originalIdx == 0) { // 0 — это correctValue
                correctId = id;
            }
            if (displayIdx > 0) {
                renderedOptions.append('\n');
            }
            renderedOptions.append(optionsFormat
                .replace("{id}", id)
                .replace("{value}", String.valueOf(value)));
        }

        if (correctId == null) {
            throw new AssessmentItemGenerationException(
                template.getId(), template.getCode(),
                "internal error: correct option id not assigned"
            );
        }

        Map<String, Object> correctAnswer = Map.of("selected", correctId);
        return new McqResult(correctAnswer, renderedOptions.toString(), optionOrder);
    }

    // ============================================================
    // OUTPUT_PREDICT / BUG_FIND
    // ============================================================
    private Object evaluateAnswer(AssessmentTemplate template, Map<String, Object> parameters) {
        Map<String, Object> answerSpec = template.getAnswerSpec();
        Object exprObj = answerSpec.get("expr");
        if (!(exprObj instanceof String expr)) {
            throw new AssessmentItemGenerationException(
                template.getId(), template.getCode(),
                "answer_spec must contain 'expr' string for type " + template.getType()
            );
        }
        return expressionEvaluator.evaluate(expr, parameters);
    }

    // ============================================================
    // Вспомогательный контейнер
    // ============================================================
    private record McqResult(Map<String, Object> correctAnswer, String renderedOptions, List<String> optionOrder) {}
}