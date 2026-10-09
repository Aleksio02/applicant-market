package com.maboy.applicantmarket.matching.model;

import java.util.Set;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankFactors {

    private UUID applicantId;

    // baseScore
    private double baseScore;
    private UUID verifiedGradeId;
    private int verifiedLevel;
    private int requiredLevel;
    private Double assessmentScore;

    // FSP
    private int fspAchievementCount;
    private double fspBoost;

    // matchLevel
    private double skillMatch;
    private double gradeMatch;
    private double matchLevel;
    private List<UUID> matchedSkillIds;
    private List<UUID> missingSkillIds;

    // freshness
    private Instant lastActivityAt;
    private double freshnessFactor;

    private Set<UUID> allSkillIds;          // все навыки кандидата
    private List<UUID> partialSkillIds;     // заявленные, но не verified
    private Set<UUID> verifiedSkillIds;     // verified

    // итог
    private double rankScore;
}