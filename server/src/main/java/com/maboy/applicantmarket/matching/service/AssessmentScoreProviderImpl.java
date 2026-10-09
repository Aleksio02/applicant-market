package com.maboy.applicantmarket.matching.service;

import com.maboy.applicantmarket.assessment.dao.AssessmentSessionDao;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssessmentScoreProviderImpl implements AssessmentScoreProvider {

    private final AssessmentSessionDao sessionDao;

    @Override
    public Double getLastScore(UUID applicantId, UUID skillId) {
        return sessionDao.findTopByApplicantIdAndSkillIdAndStatusOrderByCompletedAtDesc(
                applicantId, skillId, "COMPLETED")
                .map(s -> s.getScore() == null ? null : s.getScore().doubleValue())
                .orElse(null);
    }
}