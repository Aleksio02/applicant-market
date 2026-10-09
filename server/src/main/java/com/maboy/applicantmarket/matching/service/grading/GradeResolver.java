package com.maboy.applicantmarket.matching.service.grading;

import com.maboy.applicantmarket.commons.dao.GradeDao;
import com.maboy.applicantmarket.commons.dao.dto.GradeDto;
import com.maboy.applicantmarket.commons.exception.matching.MatchingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GradeResolver {

    private final GradeDao gradeDao;

    public int getLevel(UUID gradeId) {
        return gradeDao.findById(gradeId)
                .map(GradeDto::getLevel)
                .orElseThrow(() -> new MatchingException("Grade not found: " + gradeId));
    }

    public Optional<GradeDto> findById(UUID gradeId) {
        return gradeDao.findById(gradeId);
    }
}