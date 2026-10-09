package com.maboy.applicantmarket.matching.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResolvedCategory {
    private UUID applicantId;
    private UUID primarySkillId;
    private UUID primaryGradeId;
    private int gradeLevel;
    private UUID specializationId;
    private UUID categoryId;
    private String categoryCode;
    private String categoryName;
}