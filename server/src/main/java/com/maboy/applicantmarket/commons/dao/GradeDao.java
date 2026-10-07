package com.maboy.applicantmarket.commons.dao;

import com.maboy.applicantmarket.commons.dao.dto.GradeDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GradeDao extends JpaRepository<GradeDto, UUID> {

    Optional<GradeDto> findByCode(String code);

    List<GradeDto> findAllByOrderByLevelAsc();
}