package com.maboy.applicantmarket.assessment.service.expression;

import java.util.Map;

public interface ExpressionEvaluator {
    Object evaluate(String expr, Map<String, Object> parameters);
}