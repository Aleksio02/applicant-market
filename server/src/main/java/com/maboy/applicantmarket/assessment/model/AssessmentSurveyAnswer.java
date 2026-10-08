package com.maboy.applicantmarket.assessment.model;

import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// TODO: for future
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentSurveyAnswer {
    private UUID id;
    private UUID sessionId;
    private String questionCode;
    private Map<String, Object> answerJson;
}