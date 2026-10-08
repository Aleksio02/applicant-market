package com.maboy.applicantmarket.interaction.dao;

import com.maboy.applicantmarket.interaction.dao.dto.InteractionDto;
import com.maboy.applicantmarket.interaction.model.enums.InteractionStatus;
import com.maboy.applicantmarket.interaction.model.enums.InteractionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InteractionDao extends JpaRepository<InteractionDto, UUID> {

    List<InteractionDto> findAllByCandidateIdAndTypeOrderByCreatedAtDesc(UUID candidateId, InteractionType type);

    List<InteractionDto> findAllByEmployerIdAndTypeOrderByCreatedAtDesc(UUID employerId, InteractionType type);

    Optional<InteractionDto> findByIdAndCandidateId(UUID id, UUID candidateId);

    Optional<InteractionDto> findByIdAndEmployerId(UUID id, UUID employerId);

    boolean existsByCandidateIdAndVacancyIdAndTypeAndStatusIn(
            UUID candidateId,
            UUID vacancyId,
            InteractionType type,
            List<InteractionStatus> statuses);

    boolean existsByEmployerIdAndCandidateIdAndVacancyIdAndTypeAndStatusIn(
            UUID employerId,
            UUID candidateId,
            UUID vacancyId,
            InteractionType type,
            List<InteractionStatus> statuses);

    boolean existsByEmployerIdAndCandidateIdAndVacancyIdIsNullAndTypeAndStatusIn(
            UUID employerId,
            UUID candidateId,
            InteractionType type,
            List<InteractionStatus> statuses);
}