package com.maboy.applicantmarket.matching.dao.dto;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "matching_queries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchingQueryDto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "employer_id", nullable = false)
    private UUID employerId;

    @Column(name = "vacancy_id")
    private UUID vacancyId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "params_json", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> paramsJson;

    @Column(name = "result_count", nullable = false)
    private Integer resultCount;

    @Column(nullable = false)
    private Integer page;

    @Column(name = "page_size", nullable = false)
    private Integer pageSize;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
        if (resultCount == null) resultCount = 0;
        if (page == null) page = 0;
        if (pageSize == null) pageSize = 20;
    }
}