package com.maboy.applicantmarket.applicant.converter;

import com.maboy.applicantmarket.applicant.dao.dto.ApplicantEducationDto;
import com.maboy.applicantmarket.applicant.model.ApplicantEducation;
import com.maboy.applicantmarket.applicant.model.request.AddEducationRequest;
import com.maboy.applicantmarket.applicant.model.request.UpdateEducationRequest;
import com.maboy.applicantmarket.applicant.model.response.ApplicantEducationResponse;
import org.springframework.stereotype.Component;

@Component
public class ApplicantEducationConverter {

    public ApplicantEducation toModel(ApplicantEducationDto e) {
        if (e == null) return null;
        return ApplicantEducation.builder()
                .id(e.getId())
                .applicantId(e.getApplicantId())
                .institution(e.getInstitution())
                .degree(e.getDegree())
                .field(e.getField())
                .startYear(e.getStartYear())
                .endYear(e.getEndYear())
                .build();
    }

    public ApplicantEducationDto toEntity(ApplicantEducation m) {
        if (m == null) return null;
        return ApplicantEducationDto.builder()
                .id(m.getId())
                .applicantId(m.getApplicantId())
                .institution(m.getInstitution())
                .degree(m.getDegree())
                .field(m.getField())
                .startYear(m.getStartYear())
                .endYear(m.getEndYear())
                .build();
    }

    public ApplicantEducationResponse toResponse(ApplicantEducation m) {
        if (m == null) return null;
        ApplicantEducationResponse r = new ApplicantEducationResponse();
        r.setId(m.getId());
        r.setInstitution(m.getInstitution());
        r.setDegree(m.getDegree());
        r.setField(m.getField());
        r.setStartYear(m.getStartYear());
        r.setEndYear(m.getEndYear());
        return r;
    }

    public void applyAdd(ApplicantEducation m, AddEducationRequest r) {
        m.setInstitution(r.getInstitution());
        m.setDegree(r.getDegree());
        m.setField(r.getField());
        m.setStartYear(r.getStartYear());
        m.setEndYear(r.getEndYear());
    }

    public void applyUpdate(ApplicantEducation m, UpdateEducationRequest r) {
        if (r.getInstitution() != null) m.setInstitution(r.getInstitution());
        if (r.getDegree() != null) m.setDegree(r.getDegree());
        if (r.getField() != null) m.setField(r.getField());
        if (r.getStartYear() != null) m.setStartYear(r.getStartYear());
        if (r.getEndYear() != null) m.setEndYear(r.getEndYear());
    }
}