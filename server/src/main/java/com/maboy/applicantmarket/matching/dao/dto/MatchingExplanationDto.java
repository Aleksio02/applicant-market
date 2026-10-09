package com.maboy.applicantmarket.matching.dao.dto;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "matching_explanations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchingExplanationDto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "query_id", nullable = false)
    private UUID queryId;

    @Column(name = "applicant_id", nullable = false)
    private UUID applicantId;

    @Column(name = "rank_position", nullable = false)
    private Integer rankPosition;

    @Column(name = "rank_score", nullable = false, precision = 6, scale = 5)
    private BigDecimal rankScore;

    @Column(name = "match_level", nullable = false, precision = 6, scale = 5)
    private BigDecimal matchLevel;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "reasons_json", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> reasonsJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "factors_json", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> factorsJson;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }
}