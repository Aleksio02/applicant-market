package com.maboy.applicantmarket.vacancy.dao.dto;

import com.maboy.applicantmarket.commons.dao.dto.AbstractEntityDto;
import com.maboy.applicantmarket.commons.model.enums.WorkFormat;
import com.maboy.applicantmarket.vacancy.model.enums.VacancyStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "public", name = "vacancies")
public class VacancyDto extends AbstractEntityDto {

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text", nullable = false)
    private String description;

    @Column(name = "specialization_id", nullable = false)
    private UUID specializationId;

    @Column(name = "grade_id", nullable = false)
    private UUID gradeId;

    @Column(name = "salary_from", nullable = false)
    private Long salaryFrom;

    @Column(name = "salary_to", nullable = false)
    private Long salaryTo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkFormat format;

    @Column
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VacancyStatus status;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "closed_at")
    private Instant closedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}