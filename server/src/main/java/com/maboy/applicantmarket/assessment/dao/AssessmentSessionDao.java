package com.maboy.applicantmarket.assessment.dao;

import com.maboy.applicantmarket.assessment.dao.dto.AssessmentSessionDto;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssessmentSessionDao extends JpaRepository<AssessmentSessionDto, UUID> {

    Optional<AssessmentSessionDto> findByApplicantIdAndSkillIdAndStatusIn(
        UUID applicantId, UUID skillId, List<String> statuses
    );

    List<AssessmentSessionDto> findAllByApplicantIdOrderByStartedAtDesc(UUID applicantId);

    Optional<AssessmentSessionDto> findByIdAndApplicantId(UUID id, UUID applicantId);

    @Modifying
    @Query("""
        UPDATE AssessmentSessionDto s
        SET s.status = 'EXPIRED',
            s.completedAt = :now,
            s.updatedAt = :now
        WHERE s.status IN ('SURVEY','IN_PROGRESS')
          AND s.expiresAt < :now
        """)
    int expireOverdue(@Param("now") Instant now);
}