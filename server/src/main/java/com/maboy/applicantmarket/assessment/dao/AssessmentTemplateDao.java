package com.maboy.applicantmarket.assessment.dao;

import com.maboy.applicantmarket.assessment.dao.dto.AssessmentTemplateDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssessmentTemplateDao extends JpaRepository<AssessmentTemplateDto, UUID> {

    Optional<AssessmentTemplateDto> findByCode(String code);

    List<AssessmentTemplateDto> findAllBySkillIdAndDifficultyAndIsActiveTrue(UUID skillId, Short difficulty);

    List<AssessmentTemplateDto> findAllBySkillIdAndIsActiveTrue(UUID skillId);

    @Query("""
        SELECT DISTINCT t.difficulty
        FROM AssessmentTemplateDto t
        WHERE t.skillId = :skillId AND t.isActive = true
        """)
    List<Short> findDistinctDifficultiesForSkill(@Param("skillId") UUID skillId);
}