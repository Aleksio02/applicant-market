package com.maboy.applicantmarket.commons.service;

import com.maboy.applicantmarket.assessment.model.exception.GradeResolutionException;
import com.maboy.applicantmarket.commons.dao.GradeDao;
import com.maboy.applicantmarket.commons.dao.dto.GradeDto;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GradeResolver {

    private final GradeDao gradeDao;

    /**
     * Возвращает уровень грейда (по полю level).
     */
    public int getLevel(UUID gradeId) {
        return gradeDao.findById(gradeId)
                .map(GradeDto::getLevel)
                .orElseThrow(() -> new GradeResolutionException("Grade not found: " + gradeId));
    }

    /**
     * Находит следующий грейд выше указанного по level.
     * Если такого нет (уже максимум) — пустой Optional.
     */
    public Optional<GradeDto> findNextGrade(UUID gradeId) {
        GradeDto current = gradeDao.findById(gradeId)
                .orElseThrow(() -> new GradeResolutionException("Grade not found: " + gradeId));
        return gradeDao.findByLevel(current.getLevel() + 1);
    }

    /**
     * Находит грейд ниже указанного по level.
     * Если такого нет — пустой Optional.
     */
    public Optional<GradeDto> findPreviousGrade(UUID gradeId) {
        GradeDto current = gradeDao.findById(gradeId)
                .orElseThrow(() -> new GradeResolutionException("Grade not found: " + gradeId));
        return gradeDao.findByLevel(current.getLevel() - 1);
    }
}