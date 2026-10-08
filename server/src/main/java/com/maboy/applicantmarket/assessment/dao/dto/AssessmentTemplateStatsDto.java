package com.maboy.applicantmarket.assessment.dao.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "assessment_template_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentTemplateStatsDto {

    @Id
    @Column(name = "template_id")
    private UUID templateId;

    @Column(name = "served_count", nullable = false)
    private Integer servedCount;

    @Column(name = "correct_count", nullable = false)
    private Integer correctCount;

    @Column(name = "correct_rate", precision = 5, scale = 4)
    private BigDecimal correctRate;

    @Column(name = "discrimination_index", precision = 5, scale = 4)
    private BigDecimal discriminationIndex;

    @Column(name = "last_calculated_at")
    private Instant lastCalculatedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "generation_failure_count", nullable = false)
    private Integer generationFailureCount;

    @Column(name = "last_generation_failure_at")
    private Instant lastGenerationFailureAt;

    @PrePersist
    @PreUpdate
    void touch() {
        this.updatedAt = Instant.now();
        if (servedCount == null) {
            servedCount = 0;
        }
        if (correctCount == null) {
            correctCount = 0;
        }
    }
}