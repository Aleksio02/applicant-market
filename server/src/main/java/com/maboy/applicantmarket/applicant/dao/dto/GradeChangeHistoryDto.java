package com.maboy.applicantmarket.applicant.dao.dto;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "applicant_grade_change_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeChangeHistoryDto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "applicant_id", nullable = false)
    private UUID applicantId;

    @Column(name = "skill_id", nullable = false)
    private UUID skillId;

    @Column(name = "from_grade_id")
    private UUID fromGradeId;

    @Column(name = "to_grade_id", nullable = false)
    private UUID toGradeId;

    @Column(nullable = false, length = 20)
    private String reason;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    @PrePersist
    void onCreate() {
        this.changedAt = Instant.now();
    }
}