package com.maboy.applicantmarket.applicant.dao;

import com.maboy.applicantmarket.applicant.dao.dto.ApplicantProfileDto;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicantProfileDao extends JpaRepository<ApplicantProfileDto, UUID> {
    Optional<ApplicantProfileDto> findByUserId(UUID userId);
    boolean existsByFspId(String fspId);
    Optional<ApplicantProfileDto> findByFspId(String fspId);
}