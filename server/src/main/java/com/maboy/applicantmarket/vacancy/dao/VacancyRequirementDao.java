package com.maboy.applicantmarket.vacancy.dao;

import com.maboy.applicantmarket.vacancy.dao.dto.VacancyRequirementDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VacancyRequirementDao extends JpaRepository<VacancyRequirementDto, UUID> {

    List<VacancyRequirementDto> findAllByVacancyIdOrderByCreatedAtAsc(UUID vacancyId);

    Optional<VacancyRequirementDto> findByIdAndVacancyId(UUID id, UUID vacancyId);

    boolean existsByVacancyIdAndSkillId(UUID vacancyId, UUID skillId);
}