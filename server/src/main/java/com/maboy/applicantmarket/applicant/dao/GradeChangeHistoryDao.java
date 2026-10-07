package com.maboy.applicantmarket.applicant.dao;

import com.maboy.applicantmarket.applicant.dao.dto.GradeChangeHistoryDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeChangeHistoryDao extends JpaRepository<GradeChangeHistoryDto, UUID> {
    List<GradeChangeHistoryDto> findAllByApplicantIdOrderByChangedAtDesc(UUID applicantId);
    Optional<GradeChangeHistoryDto> findTopByApplicantIdAndSkillIdOrderByChangedAtDesc(UUID applicantId, UUID skillId);
}