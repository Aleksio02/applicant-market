package com.maboy.applicantmarket.applicant.converter;

import com.maboy.applicantmarket.applicant.dao.dto.GradeChangeHistoryDto;
import com.maboy.applicantmarket.applicant.model.response.GradeHistoryResponse;
import org.springframework.stereotype.Component;

@Component
public class GradeHistoryConverter {

    public GradeHistoryResponse toResponse(GradeChangeHistoryDto e) {
        if (e == null) return null;
        GradeHistoryResponse r = new GradeHistoryResponse();
        r.setId(e.getId());
        r.setSkillId(e.getSkillId());
        r.setFromGradeId(e.getFromGradeId());
        r.setToGradeId(e.getToGradeId());
        r.setReason(e.getReason());
        r.setChangedAt(e.getChangedAt());
        return r;
    }
}