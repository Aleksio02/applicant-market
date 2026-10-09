package com.maboy.applicantmarket.matching.service;

import com.maboy.applicantmarket.applicant.api.ApplicantModuleApi;
import com.maboy.applicantmarket.applicant.api.model.ApplicantSkillRef;
import com.maboy.applicantmarket.applicant.api.model.ApplicantSummary;
import com.maboy.applicantmarket.applicant.model.ApplicantSkill;
import com.maboy.applicantmarket.commons.dao.GradeDao;
import com.maboy.applicantmarket.commons.dao.SkillDao;
import com.maboy.applicantmarket.commons.dao.dto.GradeDto;
import com.maboy.applicantmarket.commons.dao.dto.SkillDto;
import com.maboy.applicantmarket.matching.config.MatchingProperties;
import com.maboy.applicantmarket.matching.model.RankFactors;
import com.maboy.applicantmarket.matching.model.RankedCandidate;
import com.maboy.applicantmarket.matching.model.ResolvedCategory;
import com.maboy.applicantmarket.matching.model.request.CandidateSearchRequest;
import com.maboy.applicantmarket.matching.service.grading.GradeResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingService {

    private final MatchingProperties properties;
    private final ApplicantModuleApi applicantModuleApi;
    private final AssessmentScoreProvider assessmentScoreProvider;
    private final GradeResolver gradeResolver;
    private final SkillDao skillDao;
    private final GradeDao gradeDao;

    public RankedCandidate rank(UUID applicantId,
        CandidateSearchRequest request,
        ResolvedCategory resolved) {

        RankFactors factors = computeFactors(applicantId, request, resolved);

        double rankScore = factors.getBaseScore()
                           * factors.getFspBoost()
                           * factors.getMatchLevel()
                           * factors.getFreshnessFactor();

        factors.setRankScore(rankScore);

        // Протаскиваем UI-данные
        ApplicantSummary summary = applicantModuleApi.getSummaries(List.of(applicantId))
            .get(applicantId);

        String skillName = null;
        if (resolved.getPrimarySkillId() != null) {
            skillName = skillDao.findById(resolved.getPrimarySkillId())
                .map(SkillDto::getName).orElse(null);
        }
        String gradeName = null;
        if (resolved.getPrimaryGradeId() != null) {
            gradeName = gradeDao.findById(resolved.getPrimaryGradeId())
                .map(GradeDto::getName).orElse(null);
        }

        return RankedCandidate.builder()
            .applicantId(applicantId)
            .rankScore(rankScore)
            .matchLevel(factors.getMatchLevel())
            .factors(factors)
            .displayName(summary != null ? summary.getDisplayName() : null)
            .city(summary != null ? summary.getCity() : null)
            .experienceYears(summary != null ? summary.getExperienceYears() : null)
            .primarySkillId(resolved.getPrimarySkillId())
            .primarySkillName(skillName)
            .primaryGradeId(resolved.getPrimaryGradeId())
            .primaryGradeName(gradeName)
            .categoryId(resolved.getCategoryId())
            .categoryName(resolved.getCategoryName())
            .fspAchievementCount(factors.getFspAchievementCount())
            .build();
    }

    // ============================================================
    // computeFactors
    // ============================================================
    private RankFactors computeFactors(UUID applicantId,
        CandidateSearchRequest request,
        ResolvedCategory resolved) {

        RankFactors f = RankFactors.builder()
            .applicantId(applicantId)
            .verifiedGradeId(resolved.getPrimaryGradeId())
            .verifiedLevel(resolved.getGradeLevel())
            .requiredLevel(gradeResolver.getLevel(request.getGradeId()))
            .build();

        // 1. assessment score по primary-навыку
        Double assessmentScore = assessmentScoreProvider
            .getLastScore(applicantId, resolved.getPrimarySkillId());
        f.setAssessmentScore(assessmentScore);

        // 2. baseScore
        f.setBaseScore(computeBaseScore(f));

        // 3. FSP boost
        int fspCount = applicantModuleApi.hasFspHistory(applicantId)
            ? applicantModuleApi.getFspAchievements(applicantId).size()
            : 0;
        f.setFspAchievementCount(fspCount);
        f.setFspBoost(computeFspBoost(fspCount));

        // 4. verified skills и все skills кандидата — для skillMatch
        Set<UUID> verifiedSkillIds = applicantModuleApi
            .getVerifiedSkillIdsForAll(List.of(applicantId))
            .getOrDefault(applicantId, Set.of());

        Set<UUID> allSkillIds = applicantModuleApi
            .getAllSkillIdsForAll(List.of(applicantId))
            .getOrDefault(applicantId, Set.of());

        f.setVerifiedSkillIds(verifiedSkillIds);   // добавь поле в RankFactors
        f.setAllSkillIds(allSkillIds);

        // 5. gradeMatch
        f.setGradeMatch(computeGradeMatch(f.getVerifiedLevel(), f.getRequiredLevel()));

        // 6. matchLevel = wSkill * skillMatch + wGrade * gradeMatch
        double matchLevel = computeMatchLevel(
            request.getSkillIds(), f.getVerifiedSkillIds(), f.getAllSkillIds(), f);
        f.setMatchLevel(matchLevel);

        // 7. freshness
        Instant lastActivityAt = null; // TODO: пока неоткуда взять
        f.setLastActivityAt(lastActivityAt);
        f.setFreshnessFactor(computeFreshness(lastActivityAt));

        return f;
    }

    // ============================================================
    // baseScore
    // ============================================================
    private double computeBaseScore(RankFactors f) {
        if (f.getVerifiedGradeId() == null) {
            return 0.0;
        }
        double base;
        if (f.getVerifiedLevel() == f.getRequiredLevel()) {
            base = properties.getRanking().getBaseVerifiedMatch();
        } else if (f.getVerifiedLevel() > f.getRequiredLevel()) {
            base = properties.getRanking().getBaseVerifiedAbove();
        } else {
            base = properties.getRanking().getBaseVerifiedBelow();
        }

        Double score = f.getAssessmentScore();
        if (score != null) {
            double w = properties.getRanking().getAssessmentScoreWeight();
            base = base * (1 - w) + score * w;
        }
        return clamp01(base);
    }

    // ============================================================
    // fspBoost
    // ============================================================
    private double computeFspBoost(int count) {
        if (count <= 0) return 1.0;
        double boost = 1.0 + properties.getRanking().getFspBoostPerAchievement() * Math.min(count, 10);
        return Math.min(boost, properties.getRanking().getFspBoostCap());
    }

    // ============================================================
    // gradeMatch
    // ============================================================
    private double computeGradeMatch(int verifiedLevel, int requiredLevel) {
        if (requiredLevel <= 0) return 1.0;
        if (verifiedLevel >= requiredLevel) return 1.0;
        return (double) verifiedLevel / requiredLevel;
    }

    // ============================================================
    // matchLevel
    // ============================================================
    private double computeMatchLevel(List<UUID> requiredSkillIds,
        Set<UUID> verifiedSkillIds,
        Set<UUID> allSkillIds,
        RankFactors factors) {

        if (requiredSkillIds == null || requiredSkillIds.isEmpty()) {
            factors.setSkillMatch(1.0);
            factors.setMatchedSkillIds(List.of());
            factors.setPartialSkillIds(List.of());
            factors.setMissingSkillIds(List.of());
            return factors.getGradeMatch();
        }

        List<UUID> matched = new ArrayList<>();
        List<UUID> partial = new ArrayList<>();
        List<UUID> missing = new ArrayList<>();

        for (UUID skillId : requiredSkillIds) {
            if (verifiedSkillIds.contains(skillId)) {
                matched.add(skillId);
            } else if (allSkillIds.contains(skillId)) {
                partial.add(skillId);
            } else {
                missing.add(skillId);
            }
        }

        factors.setMatchedSkillIds(matched);
        factors.setPartialSkillIds(partial);
        factors.setMissingSkillIds(missing);

        // В числитель идут ТОЛЬКО verified
        double skillMatch = (double) matched.size() / requiredSkillIds.size();
        factors.setSkillMatch(skillMatch);

        double wSkill = properties.getRanking().getSkillWeight();
        double wGrade = properties.getRanking().getGradeWeight();
        return wSkill * skillMatch + wGrade * factors.getGradeMatch();
    }

    // ============================================================
    // freshness
    // ============================================================
    private double computeFreshness(Instant lastActivityAt) {
        double floor = properties.getRanking().getFreshnessFloor();
        if (lastActivityAt == null) {
            return floor;
        }
        long days = Duration.between(lastActivityAt, Instant.now()).toDays();
        int threshold = properties.getRanking().getFreshnessThresholdDays();

        if (days <= threshold) return 1.0;
        if (days >= threshold * 6L) return floor;

        double t = (double) (days - threshold) / (threshold * 5L);
        return 1.0 - t * (1.0 - floor);
    }

    // ============================================================
    // utils
    // ============================================================
    private double clamp01(double v) {
        if (v < 0) return 0;
        if (v > 1) return 1;
        return v;
    }
}