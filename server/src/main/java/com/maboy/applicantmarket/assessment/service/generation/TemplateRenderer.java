package com.maboy.applicantmarket.assessment.service.generation;

import java.util.Map;

public interface TemplateRenderer {
    String renderBody(String bodyTemplate, Map<String, Object> parameters, Map<String, String> extraPlaceholders);
}