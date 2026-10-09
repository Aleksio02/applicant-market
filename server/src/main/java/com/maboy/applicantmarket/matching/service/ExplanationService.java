package com.maboy.applicantmarket.matching.service;

import com.maboy.applicantmarket.commons.dao.GradeDao;
import com.maboy.applicantmarket.commons.dao.SkillDao;
import com.maboy.applicantmarket.commons.dao.dto.GradeDto;
import com.maboy.applicantmarket.commons.dao.dto.SkillDto;
import com.maboy.applicantmarket.matching.model.MatchExplanation;
import com.maboy.applicantmarket.matching.model.RankFactors;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExplanationService {

    private final SkillDao skillDao;
    private final GradeDao gradeDao;

    public List<MatchExplanation> explain(RankFactors f) {
        List<MatchExplanation> result = new ArrayList<>();

        // Grade match
        if (f.getVerifiedGradeId() != null) {
            String gradeName = gradeDao.findById(f.getVerifiedGradeId())
                    .map(GradeDto::getName).orElse("?");
            double gradeWeight = f.getGradeMatch();
            result.add(new MatchExplanation(
                    "GRADE_MATCH",
                    "Подтверждённый грейд " + gradeName,
                    round(gradeWeight)
            ));
        }

        // Skill match
        if (f.getMatchedSkillIds() != null && !f.getMatchedSkillIds().isEmpty()) {
            String names = f.getMatchedSkillIds().stream()
                    .map(id -> skillDao.findById(id).map(SkillDto::getName).orElse("?"))
                    .collect(Collectors.joining(", "));
            result.add(new MatchExplanation(
                    "SKILL_MATCH",
                    "Совпало " + f.getMatchedSkillIds().size() + " навыков: " + names,
                    round(f.getSkillMatch())
            ));
        }

        // Partial skills — заявлены, но не подтверждены
        if (f.getPartialSkillIds() != null && !f.getPartialSkillIds().isEmpty()) {
            String names = f.getPartialSkillIds().stream()
                .map(id -> skillDao.findById(id).map(SkillDto::getName).orElse("?"))
                .collect(Collectors.joining(", "));
            result.add(new MatchExplanation(
                "SKILL_MATCH_PARTIAL",
                "Заявлены, но не подтверждены экзаменом: " + names,
                0.0
            ));
        }

        // Missing skills
        if (f.getMissingSkillIds() != null && !f.getMissingSkillIds().isEmpty()) {
            String names = f.getMissingSkillIds().stream()
                    .map(id -> skillDao.findById(id).map(SkillDto::getName).orElse("?"))
                    .collect(Collectors.joining(", "));
            result.add(new MatchExplanation(
                    "MISSING_SKILLS",
                    "Нет навыков: " + names,
                    -0.05 * f.getMissingSkillIds().size()
            ));
        }

        // FSP
        if (f.getFspAchievementCount() > 0) {
            result.add(new MatchExplanation(
                    "FSP_ACHIEVEMENTS",
                    f.getFspAchievementCount() + " достижений ФСП",
                    round(f.getFspBoost() - 1.0)
            ));
        }

        // Assessment score
        if (f.getAssessmentScore() != null) {
            result.add(new MatchExplanation(
                    "ASSESSMENT_SCORE",
                    "Оценка assessment: " + Math.round(f.getAssessmentScore() * 100) + "%",
                    round(f.getAssessmentScore())
            ));
        }

        // Freshness
        if (f.getFreshnessFactor() < 1.0) {
            result.add(new MatchExplanation(
                    "FRESHNESS",
                    "Давняя активность",
                    round(f.getFreshnessFactor() - 1.0)
            ));
        } else if (f.getFreshnessFactor() == 1.0) {
            result.add(new MatchExplanation(
                    "FRESHNESS",
                    "Свежая активность",
                    0.05
            ));
        }

        return result;
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}