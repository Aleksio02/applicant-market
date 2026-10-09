package com.maboy.applicantmarket.applicant.dao;

import com.maboy.applicantmarket.applicant.dao.dto.ApplicantPrivacySettingsDto;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicantPrivacySettingsDao extends JpaRepository<ApplicantPrivacySettingsDto, UUID> {

    List<ApplicantPrivacySettingsDto> findAllByApplicantIdIn(Collection<UUID> applicantIds);
}