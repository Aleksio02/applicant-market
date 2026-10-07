package com.maboy.applicantmarket.commons.dao;

import com.maboy.applicantmarket.commons.dao.dto.CategoryDto;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryDao extends JpaRepository<CategoryDto, UUID> {
    Optional<CategoryDto> findBySpecializationIdAndGradeId(UUID specializationId, UUID gradeId);
}