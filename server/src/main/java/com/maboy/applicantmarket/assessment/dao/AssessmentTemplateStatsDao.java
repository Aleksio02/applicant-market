package com.maboy.applicantmarket.assessment.dao;

import com.maboy.applicantmarket.assessment.dao.dto.AssessmentTemplateStatsDto;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentTemplateStatsDao extends JpaRepository<AssessmentTemplateStatsDto, UUID> {
}