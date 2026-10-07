package com.maboy.applicantmarket.applicant.dao;

import com.maboy.applicantmarket.applicant.dao.dto.FspAchievementStubDto;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FspAchievementStubDao extends JpaRepository<FspAchievementStubDto, UUID> {
    List<FspAchievementStubDto> findAllByApplicantIdOrderByEventDateDesc(UUID applicantId);
    void deleteAllByApplicantId(UUID applicantId);
}