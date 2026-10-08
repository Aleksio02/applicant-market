package com.maboy.applicantmarket.assignment.dao.dto;

import com.maboy.applicantmarket.assignment.model.enums.AssignmentAttemptStatus;
import com.maboy.applicantmarket.assignment.model.enums.EvaluationVerdict;
import com.maboy.applicantmarket.commons.dao.dto.AbstractEntityDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "public", name = "assignment_attempts")
public class AssignmentAttemptDto extends AbstractEntityDto {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assignment_id", nullable = false)
    private VacancyAssignmentDto assignment;

    @Column(name = "candidate_id", nullable = false)
    private UUID candidateId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentAttemptStatus status;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "deadline_at", nullable = false)
    private Instant deadlineAt;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "content_text", columnDefinition = "text")
    private String contentText;

    @Column(name = "evaluated_at")
    private Instant evaluatedAt;

    @Column
    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column
    private EvaluationVerdict verdict;

    @Column(columnDefinition = "text")
    private String feedback;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (this.createdAt == null) this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}