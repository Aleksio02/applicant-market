package com.maboy.applicantmarket.assessment.dao.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "assessment_answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentAnswerDto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "item_id", nullable = false, unique = true)
    private UUID itemId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "answer_json", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> answerJson;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @Column(nullable = false)
    private Short score;

    @Column(name = "answered_at", nullable = false)
    private Instant answeredAt;

    @Column(name = "evaluated_at", nullable = false)
    private Instant evaluatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.answeredAt = now;
        this.evaluatedAt = now;
    }
}