package com.maboy.applicantmarket.applicant.converter;

import com.maboy.applicantmarket.applicant.dao.dto.FspAchievementStubDto;
import com.maboy.applicantmarket.applicant.model.FspAchievementStub;
import com.maboy.applicantmarket.applicant.model.response.FspAchievementResponse;
import org.springframework.stereotype.Component;

@Component
public class FspAchievementConverter {

    public FspAchievementStub toModel(FspAchievementStubDto e) {
        if (e == null) {
            return null;
        }
        return FspAchievementStub.builder()
            .id(e.getId())
            .eventName(e.getEventName())
            .eventDate(e.getEventDate())
            .place(e.getPlace())
            .category(e.getCategory())
            .verified(Boolean.TRUE.equals(e.getVerified()))
            .source(e.getSource())
            .build();
    }

    public FspAchievementResponse toResponse(FspAchievementStub m) {
        if (m == null) {
            return null;
        }
        FspAchievementResponse r = new FspAchievementResponse();
        r.setId(m.getId());
        r.setEventName(m.getEventName());
        r.setEventDate(m.getEventDate());
        r.setPlace(m.getPlace());
        r.setCategory(m.getCategory());
        r.setVerified(m.getVerified());
        return r;
    }
}