package com.maboy.applicantmarket.assessment.service.grading;

import com.maboy.applicantmarket.assessment.config.AssessmentProperties;
import com.maboy.applicantmarket.assessment.model.AssessmentAnswer;
import com.maboy.applicantmarket.assessment.model.AssessmentItem;
import com.maboy.applicantmarket.commons.exception.assessment.AssessmentNotReadyForCompletionException;
import com.maboy.applicantmarket.commons.service.GradeResolver;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssessmentGradingService {

    private final GradeResolver gradeResolver;
    private final AssessmentProperties properties;

    public GradingOutcome grade(
        UUID claimedGradeId,
        List<AssessmentItem> items,
        List<AssessmentAnswer> answers
    ) {
        if (items == null || items.isEmpty()) {
            throw new AssessmentNotReadyForCompletionException(0, 0);
        }
        if (answers.size() != items.size()) {
            throw new AssessmentNotReadyForCompletionException(items.size(), answers.size());
        }

        Map<UUID, AssessmentAnswer> byItemId = answers.stream()
            .collect(Collectors.toMap(AssessmentAnswer::getItemId, Function.identity()));

        long achieved = 0;
        long total = 0;
        for (AssessmentItem item : items) {
            AssessmentAnswer answer = byItemId.get(item.getId());
            if (answer == null) {
                throw new AssessmentNotReadyForCompletionException(items.size(), answers.size());
            }
            total += item.getPoints();
            if (answer.isCorrect()) {
                achieved += item.getPoints();
            }
        }

        if (total == 0) {
            throw new IllegalStateException("Total points is zero — cannot compute score");
        }

        BigDecimal score = BigDecimal.valueOf(achieved)
            .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP);

        double pass = properties.getGrading().getThresholds().getPass();
        double promote = properties.getGrading().getThresholds().getPromote();

        if (score.doubleValue() < pass) {
            log.info("Grading: FAILED, claimed={}, score={} (achieved={}/{})",
                claimedGradeId, score, achieved, total
            );
            return new GradingOutcome("FAILED", null, score);
        }

        if (score.doubleValue() >= promote) {
            UUID promoted = gradeResolver.findNextGrade(claimedGradeId)
                .map(g -> g.getId())
                .orElse(claimedGradeId);
            log.info("Grading: COMPLETED (promoted), claimed={}, result={}, score={} (achieved={}/{})",
                claimedGradeId, promoted, score, achieved, total
            );
            return new GradingOutcome("COMPLETED", promoted, score);
        }

        log.info("Grading: COMPLETED, claimed={}, score={} (achieved={}/{})",
            claimedGradeId, score, achieved, total
        );
        return new GradingOutcome("COMPLETED", claimedGradeId, score);
    }
}