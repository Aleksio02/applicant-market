package com.maboy.applicantmarket.assessment.model.request;

import lombok.*;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAnswerRequest {
    private UUID itemId;
    private Map<String, Object> answer;
}