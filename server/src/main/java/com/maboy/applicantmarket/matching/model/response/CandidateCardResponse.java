package com.maboy.applicantmarket.matching.model.response;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateCardResponse {

    private UUID applicantId;
    private String displayName;
    private String city;
    private Short experienceYears;

    private UUID primarySkillId;
    private String primarySkillName;
    private UUID primaryGradeId;
    private String primaryGradeName;
    private UUID categoryId;
    private String categoryName;

    private Double assessmentScore;      // последний score по primary-навыку
    private int fspAchievementCount;

    private double matchLevel;           // 0..1
    private double rankScore;            // 0..1.5

    private List<MatchReasonResponse> reasons;
}