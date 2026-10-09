package com.maboy.applicantmarket.matching.dao;

import com.maboy.applicantmarket.matching.dao.dto.MatchingExplanationDto;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingExplanationDao extends JpaRepository<MatchingExplanationDto, UUID> {
    List<MatchingExplanationDto> findAllByQueryIdOrderByRankPositionAsc(UUID queryId);
}