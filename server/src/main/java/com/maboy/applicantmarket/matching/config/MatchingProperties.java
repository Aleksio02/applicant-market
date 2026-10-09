package com.maboy.applicantmarket.matching.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "matching")
@Getter
@Setter
public class MatchingProperties {

    private Ranking ranking = new Ranking();
    private Search search = new Search();

    @PostConstruct
    void validate() {
        if (ranking.getSkillWeight() < 0 || ranking.getGradeWeight() < 0) {
            throw new IllegalStateException("matching.ranking weights must be non-negative");
        }
        if (Math.abs(ranking.getSkillWeight() + ranking.getGradeWeight() - 1.0) > 1e-6) {
            throw new IllegalStateException(
                "matching.ranking.skill-weight + grade-weight must sum to 1.0");
        }
        if (ranking.getFspBoostCap() < 1.0) {
            throw new IllegalStateException("matching.ranking.fsp-boost-cap must be >= 1.0");
        }
        if (ranking.getFreshnessFloor() < 0 || ranking.getFreshnessFloor() > 1.0) {
            throw new IllegalStateException(
                "matching.ranking.freshness-floor must be in [0, 1]");
        }
        if (search.getDefaultPageSize() < 1 || search.getDefaultPageSize() > search.getMaxPageSize()) {
            throw new IllegalStateException(
                "matching.search.default-page-size must be in [1, max-page-size]");
        }
    }

    @Getter
    @Setter
    public static class Ranking {
        // base score от verified grade vs claimed
        private double baseVerifiedMatch = 0.5;
        private double baseVerifiedAbove = 0.7;
        private double baseVerifiedBelow = 0.3;
        // вес assessment score в baseScore
        private double assessmentScoreWeight = 0.3;
        // FSP boost
        private double fspBoostPerAchievement = 0.05;
        private double fspBoostCap = 1.5;
        // freshness
        private int freshnessThresholdDays = 30;
        private double freshnessFloor = 0.8;
        // matchLevel
        private double skillWeight = 0.7;
        private double gradeWeight = 0.3;
    }

    @Getter
    @Setter
    public static class Search {
        private int defaultPageSize = 20;
        private int maxPageSize = 100;
        private double defaultMinMatchLevel = 0.0;
    }
}