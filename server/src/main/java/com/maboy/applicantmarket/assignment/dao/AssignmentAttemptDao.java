package com.maboy.applicantmarket.assignment.dao;

import com.maboy.applicantmarket.assignment.dao.dto.AssignmentAttemptDto;
import com.maboy.applicantmarket.assignment.model.enums.EvaluationVerdict;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssignmentAttemptDao extends JpaRepository<AssignmentAttemptDto, UUID> {

    @EntityGraph(attributePaths = "assignment")
    Optional<AssignmentAttemptDto> findByAssignmentIdAndCandidateId(UUID assignmentId, UUID candidateId);

    @EntityGraph(attributePaths = "assignment")
    List<AssignmentAttemptDto> findAllByCandidateIdOrderByCreatedAtDesc(UUID candidateId);

    @EntityGraph(attributePaths = "assignment")
    List<AssignmentAttemptDto> findAllByAssignmentIdOrderByCreatedAtDesc(UUID assignmentId);

    @EntityGraph(attributePaths = "assignment")
    Optional<AssignmentAttemptDto> findByIdAndAssignmentId(UUID id, UUID assignmentId);

    boolean existsByAssignmentIdAndCandidateId(UUID assignmentId, UUID candidateId);

    @EntityGraph(attributePaths = "assignment")
    List<AssignmentAttemptDto> findAllByAssignmentVacancyIdAndVerdict(UUID vacancyId, EvaluationVerdict verdict);
}