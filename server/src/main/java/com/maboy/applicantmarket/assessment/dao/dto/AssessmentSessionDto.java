package com.maboy.applicantmarket.assessment.dao.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "assessment_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentSessionDto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "applicant_id", nullable = false)
    private UUID applicantId;

    @Column(name = "skill_id", nullable = false)
    private UUID skillId;

    @Column(name = "claimed_grade_id", nullable = false)
    private UUID claimedGradeId;

    @Column(nullable = false, length = 32)
    private String status;

    @Column(name = "result_grade_id")
    private UUID resultGradeId;

    @Column(precision = 5, scale = 4)
    private BigDecimal score;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

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
        this.startedAt = now;
        this.createdAt = now;
        this.updatedAt = now;
        if (this.version == null) {
            this.version = 0;
        }
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}