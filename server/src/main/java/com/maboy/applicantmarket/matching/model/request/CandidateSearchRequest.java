package com.maboy.applicantmarket.matching.model.request;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidateSearchRequest {

    private UUID specializationId;
    private UUID gradeId;
    private List<UUID> skillIds;

    private UUID vacancyId;

    /** Фильтры (опциональны). */
    private Double minMatchLevel;
    private String sortBy;        // RANK | MATCH_LEVEL | FRESHNESS | FSP | ASSESSMENT_SCORE
    private Integer page;         // 0-based
    private Integer size;         // по умолчанию default-page-size
}