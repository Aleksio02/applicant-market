package com.maboy.applicantmarket.assessment.service.generation;

import com.maboy.applicantmarket.assessment.model.exception.ParameterGenerationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class DefaultParameterGenerator implements ParameterGenerator {

    private static final int MAX_LIST_SIZE = 100;
    private static final int MAX_STRING_LENGTH = 1000;

    @Override
    public Map<String, Object> generate(Map<String, Object> parameterSpec, Random rnd) {
        if (parameterSpec == null || parameterSpec.isEmpty()) {
            return Map.of();
        }
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : parameterSpec.entrySet()) {
            String name = entry.getKey();
            Object spec = entry.getValue();
            if (!(spec instanceof Map<?, ?> specMap)) {
                throw new ParameterGenerationException(
                        "Parameter '" + name + "' spec must be an object, got "
                                + typeName(spec));
            }
            result.put(name, generateTopLevel(name, asStringObjectMap(specMap), rnd));
        }
        return result;
    }

    private Object generateTopLevel(String path, Map<String, Object> spec, Random rnd) {
        String type = requireString(spec, "type", path);
        return switch (type) {
            case "int"    -> generateInt(path, spec, rnd);
            case "choice" -> generateChoice(path, spec, rnd);
            case "list"   -> generateList(path, spec, rnd);
            case "string" -> generateString(path, spec, rnd);
            default -> throw new ParameterGenerationException(
                    "Parameter '" + path + "': unknown type '" + type + "'");
        };
    }

    // ============================================================
    // int
    // ============================================================
    private Long generateInt(String path, Map<String, Object> spec, Random rnd) {
        long min = requireLong(spec, "min", path);
        long max = requireLong(spec, "max", path);
        if (min > max) {
            throw new ParameterGenerationException(
                    "Parameter '" + path + "': min (" + min + ") > max (" + max + ")");
        }
        if (min == max) {
            return min;
        }
        long bound = max - min + 1;
        return min + rnd.nextLong(bound);
    }

    // ============================================================
    // choice
    // ============================================================
    private Object generateChoice(String path, Map<String, Object> spec, Random rnd) {
        Object valuesObj = spec.get("values");
        if (!(valuesObj instanceof List<?> values)) {
            throw new ParameterGenerationException(
                    "Parameter '" + path + "' of type 'choice' requires 'values' list");
        }
        if (values.isEmpty()) {
            throw new ParameterGenerationException(
                    "Parameter '" + path + "': 'values' must not be empty");
        }
        return values.get(rnd.nextInt(values.size()));
    }

    // ============================================================
    // list
    // element spec is a nested object describing element type.
    // Nested lists are not supported: elements can be int, choice or string.
    // ============================================================
    private List<Object> generateList(String path, Map<String, Object> spec, Random rnd) {
        long sizeLong = requireLong(spec, "size", path);
        if (sizeLong < 0 || sizeLong > MAX_LIST_SIZE) {
            throw new ParameterGenerationException(
                    "Parameter '" + path + "': size must be in [0, "
                            + MAX_LIST_SIZE + "], got " + sizeLong);
        }
        int size = (int) sizeLong;

        Object elementObj = spec.get("element");
        if (!(elementObj instanceof Map<?, ?> elementMap)) {
            throw new ParameterGenerationException(
                    "Parameter '" + path + "' of type 'list' requires 'element' object");
        }
        Map<String, Object> elementSpec = asStringObjectMap(elementMap);

        List<Object> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(generateElement(path + "[" + i + "]", elementSpec, rnd));
        }
        return result;
    }

    private Object generateElement(String path, Map<String, Object> spec, Random rnd) {
        String type = requireString(spec, "type", path);
        return switch (type) {
            case "int"    -> generateInt(path, spec, rnd);
            case "choice" -> generateChoice(path, spec, rnd);
            case "string" -> generateString(path, spec, rnd);
            case "list"   -> throw new ParameterGenerationException(
                    "Nested lists are not supported at '" + path + "'");
            default -> throw new ParameterGenerationException(
                    "Parameter '" + path + "': unknown element type '" + type + "'");
        };
    }

    // ============================================================
    // string
    // ============================================================
    private String generateString(String path, Map<String, Object> spec, Random rnd) {
        String alphabet = requireString(spec, "alphabet", path);
        if (alphabet.isEmpty()) {
            throw new ParameterGenerationException(
                    "Parameter '" + path + "': 'alphabet' must not be empty");
        }
        long lengthLong = requireLong(spec, "length", path);
        if (lengthLong < 0 || lengthLong > MAX_STRING_LENGTH) {
            throw new ParameterGenerationException(
                    "Parameter '" + path + "': length must be in [0, "
                            + MAX_STRING_LENGTH + "], got " + lengthLong);
        }
        int length = (int) lengthLong;

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(alphabet.charAt(rnd.nextInt(alphabet.length())));
        }
        return sb.toString();
    }

    // ============================================================
    // helpers
    // ============================================================
    private String requireString(Map<String, Object> spec, String key, String path) {
        Object v = spec.get(key);
        if (v == null) {
            throw new ParameterGenerationException(
                    "Parameter '" + path + "': missing required field '" + key + "'");
        }
        if (!(v instanceof String s)) {
            throw new ParameterGenerationException(
                    "Parameter '" + path + "': field '" + key + "' must be a string, got "
                            + typeName(v));
        }
        return s;
    }

    private long requireLong(Map<String, Object> spec, String key, String path) {
        Object v = spec.get(key);
        if (v == null) {
            throw new ParameterGenerationException(
                    "Parameter '" + path + "': missing required field '" + key + "'");
        }
        return toLong(v, path, key);
    }

    private long toLong(Object v, String path, String key) {
        if (v instanceof Number n) {
            return n.longValue();
        }
        if (v instanceof String s) {
            try {
                return Long.parseLong(s.trim());
            } catch (NumberFormatException e) {
                throw new ParameterGenerationException(
                        "Parameter '" + path + "': field '" + key
                                + "' is not a number: " + s, e);
            }
        }
        throw new ParameterGenerationException(
                "Parameter '" + path + "': field '" + key + "' must be a number, got "
                        + typeName(v));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asStringObjectMap(Map<?, ?> map) {
        return (Map<String, Object>) map;
    }

    private String typeName(Object v) {
        return v == null ? "null" : v.getClass().getSimpleName();
    }
}