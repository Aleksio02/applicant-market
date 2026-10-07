package com.maboy.applicantmarket.commons.dao;

import com.maboy.applicantmarket.commons.dao.dto.SpecializationDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpecializationDao extends JpaRepository<SpecializationDto, UUID> {

    Optional<SpecializationDto> findByCode(String code);

    List<SpecializationDto> findAllByOrderByNameAsc();
}