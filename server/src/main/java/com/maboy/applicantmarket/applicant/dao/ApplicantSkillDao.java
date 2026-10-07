package com.maboy.applicantmarket.applicant.dao;

import com.maboy.applicantmarket.applicant.dao.dto.ApplicantSkillDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApplicantSkillDao extends JpaRepository<ApplicantSkillDto, UUID> {
    List<ApplicantSkillDto> findAllByApplicantId(UUID applicantId);
    Optional<ApplicantSkillDto> findByApplicantIdAndSkillId(UUID applicantId, UUID skillId);
    Optional<ApplicantSkillDto> findByApplicantIdAndIsPrimaryTrue(UUID applicantId);
    boolean existsByApplicantIdAndSkillId(UUID applicantId, UUID skillId);

    @Query("""
        SELECT s.applicantId
        FROM ApplicantSkillDto s
        WHERE s.skillId = :skillId
          AND s.verifiedGradeId = :gradeId
        """)
    List<UUID> findApplicantIdsBySkillAndGrade(@Param("skillId") UUID skillId,
        @Param("gradeId") UUID gradeId);
}