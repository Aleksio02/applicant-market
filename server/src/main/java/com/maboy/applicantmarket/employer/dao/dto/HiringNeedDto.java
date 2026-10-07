package com.maboy.applicantmarket.employer.dao.dto;

import com.maboy.applicantmarket.commons.dao.dto.AbstractEntityDto;
import com.maboy.applicantmarket.employer.model.enums.WorkFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "public", name = "hiring_needs")
public class HiringNeedDto extends AbstractEntityDto {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyDto company;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "specialization_id", nullable = false)
    private UUID specializationId;

    @Column(name = "grade_id", nullable = false)
    private UUID gradeId;

    @Column(name = "salary_from")
    private Long salaryFrom;

    @Column(name = "salary_to")
    private Long salaryTo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkFormat format;

    @Column
    private String location;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}