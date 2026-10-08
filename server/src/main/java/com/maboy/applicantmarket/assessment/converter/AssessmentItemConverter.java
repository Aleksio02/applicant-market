package com.maboy.applicantmarket.assessment.converter;

import com.maboy.applicantmarket.assessment.dao.dto.AssessmentItemDto;
import com.maboy.applicantmarket.assessment.model.AssessmentItem;
import com.maboy.applicantmarket.assessment.model.response.AssessmentItemResponse;
import org.springframework.stereotype.Component;

@Component
public class AssessmentItemConverter {

    public AssessmentItem toModel(AssessmentItemDto e) {
        if (e == null) {
            return null;
        }
        return AssessmentItem.builder()
            .id(e.getId())
            .sessionId(e.getSessionId())
            .templateId(e.getTemplateId())
            .templateCode(e.getTemplateCode())
            .position(e.getPosition())
            .type(e.getType())
            .topic(e.getTopic())
            .difficulty(e.getDifficulty())
            .body(e.getBody())
            .correctAnswer(e.getCorrectAnswer())
            .parameters(e.getParameters())
            .generationMeta(e.getGenerationMeta())
            .points(e.getPoints())
            .build();
    }

    public AssessmentItemDto toNewDto(AssessmentItem m) {
        if (m == null) {
            return null;
        }
        return AssessmentItemDto.builder()
            .sessionId(m.getSessionId())
            .templateId(m.getTemplateId())
            .templateCode(m.getTemplateCode())
            .position(m.getPosition())
            .type(m.getType())
            .topic(m.getTopic())
            .difficulty(m.getDifficulty())
            .body(m.getBody())
            .correctAnswer(m.getCorrectAnswer())
            .parameters(m.getParameters())
            .generationMeta(m.getGenerationMeta())
            .points(m.getPoints())
            .build();
    }

    public AssessmentItemResponse toResponse(AssessmentItem m, boolean answered) {
        if (m == null) return null;
        return AssessmentItemResponse.builder()
            .id(m.getId())
            .position(m.getPosition())
            .type(m.getType())
            .topic(m.getTopic())
            .difficulty(m.getDifficulty())
            .body(m.getBody())
            .points(m.getPoints())
            .answered(answered)
            .build();
    }
}