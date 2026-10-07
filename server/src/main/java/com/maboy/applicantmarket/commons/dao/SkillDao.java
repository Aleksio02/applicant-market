package com.maboy.applicantmarket.commons.dao;

import com.maboy.applicantmarket.commons.dao.dto.SkillDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillDao extends JpaRepository<SkillDto, UUID> {
    Optional<SkillDto> findByCode(String code);
    List<SkillDto> findAllByIsActiveTrueOrderBySortOrderAsc();
}