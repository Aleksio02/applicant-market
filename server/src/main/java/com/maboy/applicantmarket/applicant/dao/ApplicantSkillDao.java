package com.maboy.applicantmarket.applicant.dao;

import com.maboy.applicantmarket.applicant.dao.dto.ApplicantSkillDto;
import java.util.Collection;
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

    List<ApplicantSkillDto> findAllByApplicantIdInAndIsPrimaryTrue(Collection<UUID> applicantIds);

    List<ApplicantSkillDto> findAllByApplicantIdInAndVerifiedGradeIdIsNotNull(Collection<UUID> applicantIds);

    @Query("""
        SELECT s.applicantId FROM ApplicantSkillDto s
        WHERE s.isPrimary = true
          AND s.skillId IN :skillIds
          AND s.verifiedGradeId = :gradeId
        """)
    List<UUID> findApplicantIdsByPrimarySkillInAndVerifiedGrade(
        @Param("skillIds") Collection<UUID> skillIds,
        @Param("gradeId") UUID gradeId);

    @Query("""
        SELECT s.id FROM SkillDto s
        WHERE s.category = :category AND s.isActive = true
        """)
    List<UUID> findSkillsByCategory(@Param("category") String category);

    @Query(value = """
        SELECT COUNT(DISTINCT a.applicant_id)
        FROM applicant_skills a
        JOIN skills s ON s.id = a.skill_id
        WHERE a.is_primary = true
          AND a.verified_grade_id = :gradeId
          AND s.category = :categoryCode
          AND s.is_active = true
        """, nativeQuery = true)
    long countPrimaryWithGradeAndCategory(
        @Param("categoryCode") String categoryCode,
        @Param("gradeId") UUID gradeId);

    List<ApplicantSkillDto> findAllByApplicantIdIn(Collection<UUID> applicantIds);
}