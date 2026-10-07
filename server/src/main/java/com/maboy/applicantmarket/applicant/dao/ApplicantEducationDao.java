package com.maboy.applicantmarket.applicant.dao;

import com.maboy.applicantmarket.applicant.dao.dto.ApplicantEducationDto;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicantEducationDao extends JpaRepository<ApplicantEducationDto, UUID> {
    List<ApplicantEducationDto> findAllByApplicantId(UUID applicantId);
    void deleteAllByApplicantId(UUID applicantId);
}