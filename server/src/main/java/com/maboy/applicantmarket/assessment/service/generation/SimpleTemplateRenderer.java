package com.maboy.applicantmarket.assessment.service.generation;

import com.maboy.applicantmarket.assessment.model.exception.TemplateRenderException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SimpleTemplateRenderer implements TemplateRenderer {

    // {name} где name — буквы, цифры, подчёркивание. Без вложенных скобок.
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{([a-zA-Z_][a-zA-Z0-9_]*)}");

    @Override
    public String renderBody(String bodyTemplate,
                             Map<String, Object> parameters,
                             Map<String, String> extraPlaceholders) {
        if (bodyTemplate == null) {
            throw new TemplateRenderException("body_template is null");
        }
        Map<String, String> extras = extraPlaceholders == null ? Map.of() : extraPlaceholders;

        Matcher matcher = PLACEHOLDER.matcher(bodyTemplate);
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            result.append(bodyTemplate, lastEnd, matcher.start());
            String name = matcher.group(1);
            String replacement = resolve(name, parameters, extras);
            result.append(replacement);
            lastEnd = matcher.end();
        }
        result.append(bodyTemplate, lastEnd, bodyTemplate.length());
        return result.toString();
    }

    private String resolve(String name, Map<String, Object> parameters, Map<String, String> extras) {
        // Сначала extras: {options} и подобные не приходят из параметров.
        if (extras.containsKey(name)) {
            return extras.get(name);
        }
        if (parameters.containsKey(name)) {
            Object v = parameters.get(name);
            return v == null ? "" : String.valueOf(v);
        }
        throw new TemplateRenderException("Unknown placeholder '{" + name + "}'");
    }
}