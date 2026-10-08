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
public class AssessmentItem {
    private UUID id;
    private UUID sessionId;
    private UUID templateId;
    private String templateCode;
    private Integer position;
    private String type;
    private String topic;
    private Short difficulty;
    private String body;
    private Map<String, Object> correctAnswer;
    private Map<String, Object> parameters;
    private Map<String, Object> generationMeta;
    private Short points;
}