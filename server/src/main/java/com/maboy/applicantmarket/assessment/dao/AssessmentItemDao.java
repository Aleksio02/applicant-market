package com.maboy.applicantmarket.assessment.dao;

import com.maboy.applicantmarket.assessment.dao.dto.AssessmentItemDto;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentItemDao extends JpaRepository<AssessmentItemDto, UUID> {

    List<AssessmentItemDto> findAllBySessionIdOrderByPositionAsc(UUID sessionId);

    long countBySessionId(UUID sessionId);
}