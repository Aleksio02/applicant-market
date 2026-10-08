package com.maboy.applicantmarket.assignment.dao.dto;

import com.maboy.applicantmarket.commons.dao.dto.AbstractEntityDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "public", name = "vacancy_assignments")
public class VacancyAssignmentDto extends AbstractEntityDto {

    @Column(name = "vacancy_id", nullable = false, unique = true)
    private UUID vacancyId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text", nullable = false)
    private String description;

    @Column(name = "duration_hours", nullable = false)
    private Integer durationHours;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}