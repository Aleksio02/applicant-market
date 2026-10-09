package com.maboy.applicantmarket.matching.model;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankedCandidate {
    // core
    private UUID applicantId;
    private double rankScore;
    private double matchLevel;
    private RankFactors factors;
    private List<MatchExplanation> explanations;

    // UI-поля (наполняются в RankingService)
    private String displayName;
    private String city;
    private Short experienceYears;
    private UUID primarySkillId;
    private String primarySkillName;
    private UUID primaryGradeId;
    private String primaryGradeName;
    private UUID categoryId;
    private String categoryName;
    private int fspAchievementCount;
}