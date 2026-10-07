package com.maboy.applicantmarket.employer.dao;

import com.maboy.applicantmarket.employer.dao.dto.HiringNeedDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HiringNeedDao extends JpaRepository<HiringNeedDto, UUID> {

    Page<HiringNeedDto> findAllByCompanyId(UUID companyId, Pageable pageable);

    Page<HiringNeedDto> findAllByCompanyIdAndActive(UUID companyId, boolean active, Pageable pageable);

    Optional<HiringNeedDto> findByIdAndCompanyId(UUID id, UUID companyId);
}