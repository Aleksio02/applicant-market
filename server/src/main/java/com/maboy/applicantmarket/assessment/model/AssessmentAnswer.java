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
public class AssessmentAnswer {
    private UUID id;
    private UUID itemId;
    private Map<String, Object> answerJson;
    private boolean correct;
    private Short score;
}