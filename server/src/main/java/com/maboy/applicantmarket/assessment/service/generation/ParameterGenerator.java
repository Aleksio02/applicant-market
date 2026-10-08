package com.maboy.applicantmarket.assessment.service.generation;

import java.util.Map;
import java.util.Random;

public interface ParameterGenerator {
    Map<String, Object> generate(Map<String, Object> parameterSpec, Random rnd);
}