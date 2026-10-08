package com.maboy.applicantmarket.assessment.service.generation;

import com.maboy.applicantmarket.assessment.config.AssessmentProperties;
import com.maboy.applicantmarket.commons.dao.GradeDao;
import com.maboy.applicantmarket.commons.dao.dto.GradeDto;
import com.maboy.applicantmarket.commons.exception.assessment.AssessmentTemplateNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GradeDifficultyResolver {

    private final GradeDao gradeDao;
    private final AssessmentProperties properties;

    /**
     * Возвращает difficulty (1..5), соответствующий claimed-грейду.
     * Бросает исключение, если грейд не найден или не сконфигурирован.
     */
    public short resolveDifficulty(UUID gradeId) {
        GradeDto grade = gradeDao.findById(gradeId)
            .orElseThrow(() -> new AssessmentTemplateNotFoundException(
                "Grade not found: " + gradeId));
        Integer difficulty = properties.getGrading()
            .getGradeToDifficulty().get(grade.getCode());
        if (difficulty == null) {
            throw new AssessmentTemplateNotFoundException(
                "No difficulty mapping for grade code: " + grade.getCode());
        }
        return difficulty.shortValue();
    }
}