package com.maboy.applicantmarket.commons.dao;

import com.maboy.applicantmarket.commons.dao.dto.SkillDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SkillDao extends JpaRepository<SkillDto, UUID> {
    Optional<SkillDto> findByCode(String code);
    List<SkillDto> findAllByIsActiveTrueOrderBySortOrderAsc();
}