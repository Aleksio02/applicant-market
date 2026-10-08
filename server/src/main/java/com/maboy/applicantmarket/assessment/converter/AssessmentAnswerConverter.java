package com.maboy.applicantmarket.assessment.converter;

import com.maboy.applicantmarket.assessment.dao.dto.AssessmentAnswerDto;
import com.maboy.applicantmarket.assessment.model.AssessmentAnswer;
import org.springframework.stereotype.Component;

@Component
public class AssessmentAnswerConverter {

    public AssessmentAnswer toModel(AssessmentAnswerDto e) {
        if (e == null) {
            return null;
        }
        return AssessmentAnswer.builder()
            .id(e.getId())
            .itemId(e.getItemId())
            .answerJson(e.getAnswerJson())
            .correct(Boolean.TRUE.equals(e.getIsCorrect()))
            .score(e.getScore())
            .build();
    }

    public AssessmentAnswerDto toNewDto(AssessmentAnswer m) {
        if (m == null) {
            return null;
        }
        return AssessmentAnswerDto.builder()
            .itemId(m.getItemId())
            .answerJson(m.getAnswerJson())
            .isCorrect(m.isCorrect())
            .score(m.getScore())
            .build();
    }
}