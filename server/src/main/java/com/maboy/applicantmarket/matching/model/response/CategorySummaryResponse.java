package com.maboy.applicantmarket.matching.model.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorySummaryResponse {
    private UUID categoryId;
    private String categoryCode;
    private String categoryName;
    private UUID specializationId;
    private UUID gradeId;
    private long candidateCount;
}