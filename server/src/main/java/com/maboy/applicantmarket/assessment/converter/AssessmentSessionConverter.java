package com.maboy.applicantmarket.assessment.converter;

import com.maboy.applicantmarket.assessment.dao.dto.AssessmentSessionDto;
import com.maboy.applicantmarket.assessment.model.AssessmentSession;
import com.maboy.applicantmarket.assessment.model.response.AssessmentItemResponse;
import com.maboy.applicantmarket.assessment.model.response.AssessmentSessionResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AssessmentSessionConverter {

    public AssessmentSession toModel(AssessmentSessionDto e) {
        if (e == null) {
            return null;
        }
        return AssessmentSession.builder()
            .id(e.getId())
            .applicantId(e.getApplicantId())
            .skillId(e.getSkillId())
            .claimedGradeId(e.getClaimedGradeId())
            .status(e.getStatus())
            .resultGradeId(e.getResultGradeId())
            .score(e.getScore())
            .startedAt(e.getStartedAt())
            .completedAt(e.getCompletedAt())
            .expiresAt(e.getExpiresAt())
            .build();
    }

    /**
     * В отличие от applicant, здесь мы НЕ используем этот метод для update. Сессия меняется через модель и её доменные
     * методы, а обратно переносится через applyModelToDto — чтобы не терять version и createdAt.
     */
    public AssessmentSessionDto toNewDto(AssessmentSession m) {
        if (m == null) {
            return null;
        }
        return AssessmentSessionDto.builder()
            .applicantId(m.getApplicantId())
            .skillId(m.getSkillId())
            .claimedGradeId(m.getClaimedGradeId())
            .status(m.getStatus())
            .resultGradeId(m.getResultGradeId())
            .score(m.getScore())
            .expiresAt(m.getExpiresAt())
            .build();
    }

    public void applyModelToDto(AssessmentSession m, AssessmentSessionDto e) {
        e.setStatus(m.getStatus());
        e.setResultGradeId(m.getResultGradeId());
        e.setScore(m.getScore());
        e.setCompletedAt(m.getCompletedAt());
    }

    public AssessmentSessionResponse toResponse(AssessmentSession m,
        List<AssessmentItemResponse> items) {
        if (m == null) return null;
        return AssessmentSessionResponse.builder()
            .id(m.getId())
            .skillId(m.getSkillId())
            .claimedGradeId(m.getClaimedGradeId())
            .status(m.getStatus())
            .resultGradeId(m.getResultGradeId())
            .score(m.getScore())
            .startedAt(m.getStartedAt())
            .expiresAt(m.getExpiresAt())
            .completedAt(m.getCompletedAt())
            .items(items)
            .build();
    }
}