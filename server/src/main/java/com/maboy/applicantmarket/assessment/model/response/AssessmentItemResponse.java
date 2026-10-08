package com.maboy.applicantmarket.assessment.model.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentItemResponse {
    private UUID id;
    private Integer position;
    private String type;
    private String topic;
    private Short difficulty;
    private String body;
    private Short points;
    private boolean answered;
}