package com.maboy.applicantmarket.assessment.dao;

import com.maboy.applicantmarket.assessment.dao.dto.AssessmentSurveyAnswerDto;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentSurveyAnswerDao extends JpaRepository<AssessmentSurveyAnswerDto, UUID> {

    List<AssessmentSurveyAnswerDto> findAllBySessionId(UUID sessionId);
}