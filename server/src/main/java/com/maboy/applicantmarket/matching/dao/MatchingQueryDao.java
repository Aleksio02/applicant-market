package com.maboy.applicantmarket.matching.dao;

import com.maboy.applicantmarket.matching.dao.dto.MatchingQueryDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingQueryDao extends JpaRepository<MatchingQueryDto, UUID> {
    Optional<MatchingQueryDto> findByIdAndEmployerId(UUID id, UUID employerId);
    List<MatchingQueryDto> findAllByEmployerIdOrderByCreatedAtDesc(UUID employerId);
}