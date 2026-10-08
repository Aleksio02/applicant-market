package com.maboy.applicantmarket.commons.dao;

import com.maboy.applicantmarket.commons.dao.dto.CategoryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryDao extends JpaRepository<CategoryDto, UUID> {
    Optional<CategoryDto> findBySpecializationIdAndGradeId(UUID specializationId, UUID gradeId);
}