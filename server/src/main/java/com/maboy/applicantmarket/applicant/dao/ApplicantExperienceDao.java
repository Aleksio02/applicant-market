package com.maboy.applicantmarket.applicant.dao;

import com.maboy.applicantmarket.applicant.dao.dto.ApplicantExperienceDto;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicantExperienceDao extends JpaRepository<ApplicantExperienceDto, UUID> {
    List<ApplicantExperienceDto> findAllByApplicantIdOrderBySortOrderAsc(UUID applicantId);
    void deleteAllByApplicantId(UUID applicantId);
}