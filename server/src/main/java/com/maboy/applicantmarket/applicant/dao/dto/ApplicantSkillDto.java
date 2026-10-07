package com.maboy.applicantmarket.applicant.dao.dto;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "applicant_skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicantSkillDto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "applicant_id", nullable = false)
    private UUID applicantId;

    @Column(name = "skill_id", nullable = false)
    private UUID skillId;

    @Column(name = "self_assessed_level")
    private Short selfAssessedLevel;

    @Column(name = "verified_grade_id")
    private UUID verifiedGradeId;

    @Column(name = "last_grade_change_at")
    private Instant lastGradeChangeAt;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary;

    @Column(name = "years_experience", precision = 3, scale = 1)
    private BigDecimal yearsExperience;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private Integer version;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.isPrimary == null) this.isPrimary = false;
        if (this.version == null) this.version = 0;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}