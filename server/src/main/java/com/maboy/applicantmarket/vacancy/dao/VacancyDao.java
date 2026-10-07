package com.maboy.applicantmarket.vacancy.dao;

import com.maboy.applicantmarket.vacancy.dao.dto.VacancyDto;
import com.maboy.applicantmarket.vacancy.model.enums.VacancyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VacancyDao extends JpaRepository<VacancyDto, UUID>, JpaSpecificationExecutor<VacancyDto> {

    Page<VacancyDto> findAllByCompanyId(UUID companyId, Pageable pageable);

    Page<VacancyDto> findAllByCompanyIdAndStatus(UUID companyId, VacancyStatus status, Pageable pageable);

    Optional<VacancyDto> findByIdAndCompanyId(UUID id, UUID companyId);

    Page<VacancyDto> findAllByStatus(VacancyStatus status, Pageable pageable);
}