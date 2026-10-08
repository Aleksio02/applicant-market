package com.maboy.applicantmarket.assessment.model;

import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentTemplate {
    private UUID id;
    private String code;
    private UUID skillId;
    private String topic;
    private String type;
    private Short difficulty;
    private String bodyTemplate;
    private Map<String, Object> parameterSpec;
    private Map<String, Object> answerSpec;
    private String explanationTemplate;
    private boolean active;
}