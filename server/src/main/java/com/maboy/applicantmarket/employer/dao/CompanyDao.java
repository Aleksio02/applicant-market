package com.maboy.applicantmarket.employer.dao;

import com.maboy.applicantmarket.employer.dao.dto.CompanyDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyDao extends JpaRepository<CompanyDto, UUID> {

    Optional<CompanyDto> findByOwnerId(UUID ownerId);

    boolean existsByOwnerId(UUID ownerId);
}