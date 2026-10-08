package com.maboy.applicantmarket.assessment.converter;

import com.maboy.applicantmarket.assessment.dao.dto.AssessmentTemplateDto;
import com.maboy.applicantmarket.assessment.model.AssessmentTemplate;
import org.springframework.stereotype.Component;

@Component
public class AssessmentTemplateConverter {

    public AssessmentTemplate toModel(AssessmentTemplateDto e) {
        if (e == null) {
            return null;
        }
        return AssessmentTemplate.builder()
            .id(e.getId())
            .code(e.getCode())
            .skillId(e.getSkillId())
            .topic(e.getTopic())
            .type(e.getType())
            .difficulty(e.getDifficulty())
            .bodyTemplate(e.getBodyTemplate())
            .parameterSpec(e.getParameterSpec())
            .answerSpec(e.getAnswerSpec())
            .explanationTemplate(e.getExplanationTemplate())
            .active(Boolean.TRUE.equals(e.getIsActive()))
            .build();
    }

    public AssessmentTemplateDto toDto(AssessmentTemplate m) {
        if (m == null) {
            return null;
        }
        return AssessmentTemplateDto.builder()
            .id(m.getId())
            .code(m.getCode())
            .skillId(m.getSkillId())
            .topic(m.getTopic())
            .type(m.getType())
            .difficulty(m.getDifficulty())
            .bodyTemplate(m.getBodyTemplate())
            .parameterSpec(m.getParameterSpec())
            .answerSpec(m.getAnswerSpec())
            .explanationTemplate(m.getExplanationTemplate())
            .isActive(m.isActive())
            .build();
    }
}