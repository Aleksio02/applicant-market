package com.maboy.applicantmarket.assessment.dao;

import com.maboy.applicantmarket.assessment.dao.dto.AssessmentAnswerDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentAnswerDao extends JpaRepository<AssessmentAnswerDto, UUID> {

    Optional<AssessmentAnswerDto> findByItemId(UUID itemId);

    List<AssessmentAnswerDto> findAllByItemIdIn(List<UUID> itemIds);

    long countByItemIdIn(List<UUID> itemIds);
}