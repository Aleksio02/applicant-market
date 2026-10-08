package com.maboy.applicantmarket.assignment.dao;

import com.maboy.applicantmarket.assignment.dao.dto.VacancyAssignmentDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssignmentDao extends JpaRepository<VacancyAssignmentDto, UUID> {

    Optional<VacancyAssignmentDto> findByVacancyId(UUID vacancyId);

    boolean existsByVacancyId(UUID vacancyId);
}