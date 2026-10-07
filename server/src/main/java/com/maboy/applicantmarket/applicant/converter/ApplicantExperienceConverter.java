package com.maboy.applicantmarket.applicant.converter;

import com.maboy.applicantmarket.applicant.dao.dto.ApplicantExperienceDto;
import com.maboy.applicantmarket.applicant.model.ApplicantExperience;
import com.maboy.applicantmarket.applicant.model.request.AddExperienceRequest;
import com.maboy.applicantmarket.applicant.model.request.UpdateExperienceRequest;
import com.maboy.applicantmarket.applicant.model.response.ApplicantExperienceResponse;
import org.springframework.stereotype.Component;

@Component
public class ApplicantExperienceConverter {

    public ApplicantExperience toModel(ApplicantExperienceDto e) {
        if (e == null) return null;
        return ApplicantExperience.builder()
                .id(e.getId())
                .applicantId(e.getApplicantId())
                .company(e.getCompany())
                .position(e.getPosition())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .isCurrent(Boolean.TRUE.equals(e.getIsCurrent()))
                .description(e.getDescription())
                .sortOrder(e.getSortOrder())
                .build();
    }

    public ApplicantExperienceDto toEntity(ApplicantExperience m) {
        if (m == null) return null;
        return ApplicantExperienceDto.builder()
                .id(m.getId())
                .applicantId(m.getApplicantId())
                .company(m.getCompany())
                .position(m.getPosition())
                .startDate(m.getStartDate())
                .endDate(m.getEndDate())
                .isCurrent(m.getIsCurrent())
                .description(m.getDescription())
                .sortOrder(m.getSortOrder())
                .build();
    }

    public ApplicantExperienceResponse toResponse(ApplicantExperience m) {
        if (m == null) return null;
        ApplicantExperienceResponse r = new ApplicantExperienceResponse();
        r.setId(m.getId());
        r.setCompany(m.getCompany());
        r.setPosition(m.getPosition());
        r.setStartDate(m.getStartDate());
        r.setEndDate(m.getEndDate());
        r.setCurrent(m.getIsCurrent());
        r.setDescription(m.getDescription());
        r.setSortOrder(m.getSortOrder());
        return r;
    }

    public void applyAdd(ApplicantExperience m, AddExperienceRequest r) {
        m.setCompany(r.getCompany());
        m.setPosition(r.getPosition());
        m.setStartDate(r.getStartDate());
        m.setEndDate(r.getEndDate());
        m.setIsCurrent(Boolean.TRUE.equals(r.getCurrent()));
        m.setDescription(r.getDescription());
        if (r.getSortOrder() != null) m.setSortOrder(r.getSortOrder());
    }

    public void applyUpdate(ApplicantExperience m, UpdateExperienceRequest r) {
        if (r.getCompany() != null) m.setCompany(r.getCompany());
        if (r.getPosition() != null) m.setPosition(r.getPosition());
        if (r.getStartDate() != null) m.setStartDate(r.getStartDate());
        if (r.getEndDate() != null) m.setEndDate(r.getEndDate());
        if (r.getCurrent() != null) m.setIsCurrent(r.getCurrent());
        if (r.getDescription() != null) m.setDescription(r.getDescription());
        if (r.getSortOrder() != null) m.setSortOrder(r.getSortOrder());
    }
}