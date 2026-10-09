package com.maboy.applicantmarket.matching.converter;

import com.maboy.applicantmarket.matching.dao.dto.MatchingExplanationDto;
import com.maboy.applicantmarket.matching.model.MatchExplanation;
import com.maboy.applicantmarket.matching.model.RankedCandidate;
import com.maboy.applicantmarket.matching.model.response.CandidateCardResponse;
import com.maboy.applicantmarket.matching.model.response.MatchReasonResponse;
import java.util.Map;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CandidateSearchConverter {

    public CandidateCardResponse toCard(RankedCandidate rc) {
        if (rc == null) return null;
        return CandidateCardResponse.builder()
                .applicantId(rc.getApplicantId())
                .displayName(rc.getDisplayName())
                .city(rc.getCity())
                .experienceYears(rc.getExperienceYears())
                .primarySkillId(rc.getPrimarySkillId())
                .primarySkillName(rc.getPrimarySkillName())
                .primaryGradeId(rc.getPrimaryGradeId())
                .primaryGradeName(rc.getPrimaryGradeName())
                .categoryId(rc.getCategoryId())
                .categoryName(rc.getCategoryName())
                .assessmentScore(rc.getFactors().getAssessmentScore())
                .fspAchievementCount(rc.getFspAchievementCount())
                .matchLevel(rc.getMatchLevel())
                .rankScore(rc.getRankScore())
                .reasons(rc.getExplanations() == null ? List.of()
                        : rc.getExplanations().stream()
                        .map(this::toReason)
                        .toList())
                .build();
    }

    public CandidateCardResponse explanationToCard(MatchingExplanationDto dto) {
        // factors_json и reasons_json хранят всё нужное
        return CandidateCardResponse.builder()
                .applicantId(dto.getApplicantId())
                .rankScore(dto.getRankScore().doubleValue())
                .matchLevel(dto.getMatchLevel().doubleValue())
                .reasons(extractReasons(dto))
                .build();
    }

    private MatchReasonResponse toReason(MatchExplanation e) {
        return MatchReasonResponse.builder()
                .code(e.getCode())
                .text(e.getText())
                .weight(e.getWeight())
                .build();
    }

    @SuppressWarnings("unchecked")
    private List<MatchReasonResponse> extractReasons(MatchingExplanationDto dto) {
        Object reasons = dto.getReasonsJson().get("reasons");
        if (!(reasons instanceof List<?> list)) return List.of();
        return list.stream()
                .filter(Map.class::isInstance)
                .map(m -> {
                    Map<String, Object> map = (Map<String, Object>) m;
                    return MatchReasonResponse.builder()
                            .code((String) map.get("code"))
                            .text((String) map.get("text"))
                            .weight(map.get("weight") instanceof Number n ? n.doubleValue() : 0.0)
                            .build();
                })
                .toList();
    }
}