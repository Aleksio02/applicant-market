package com.maboy.applicantmarket.applicant.converter;

import com.maboy.applicantmarket.applicant.dao.dto.ApplicantProfileDto;
import com.maboy.applicantmarket.applicant.model.ApplicantProfile;
import com.maboy.applicantmarket.applicant.model.request.UpdateApplicantProfileRequest;
import com.maboy.applicantmarket.applicant.model.response.ApplicantProfileResponse;
import org.springframework.stereotype.Component;

@Component
public class ApplicantProfileConverter {

    public ApplicantProfile fromDto(ApplicantProfileDto e) {
        if (e == null) return null;
        return ApplicantProfile.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .middleName(e.getMiddleName())
                .phone(e.getPhone())
                .city(e.getCity())
                .country(e.getCountry())
                .about(e.getAbout())
                .experienceYears(e.getExperienceYears())
                .fspId(e.getFspId())
                .fspLinkedAt(e.getFspLinkedAt())
                .status(e.getStatus())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    public ApplicantProfileDto toDto(ApplicantProfile m) {
        if (m == null) return null;
        return ApplicantProfileDto.builder()
                .id(m.getId())
                .userId(m.getUserId())
                .firstName(m.getFirstName())
                .lastName(m.getLastName())
                .middleName(m.getMiddleName())
                .phone(m.getPhone())
                .city(m.getCity())
                .country(m.getCountry())
                .about(m.getAbout())
                .experienceYears(m.getExperienceYears())
                .fspId(m.getFspId())
                .fspLinkedAt(m.getFspLinkedAt())
                .status(m.getStatus())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build();
    }

    public ApplicantProfileResponse toResponse(ApplicantProfile m) {
        if (m == null) return null;
        ApplicantProfileResponse r = new ApplicantProfileResponse();
        r.setId(m.getId());
        r.setFirstName(m.getFirstName());
        r.setLastName(m.getLastName());
        r.setMiddleName(m.getMiddleName());
        r.setPhone(m.getPhone());
        r.setCity(m.getCity());
        r.setCountry(m.getCountry());
        r.setAbout(m.getAbout());
        r.setExperienceYears(m.getExperienceYears());
        r.setFspId(m.getFspId());
        r.setFspLinkedAt(m.getFspLinkedAt());
        r.setStatus(m.getStatus());
        return r;
    }

    public void applyUpdate(ApplicantProfile m, UpdateApplicantProfileRequest r) {
        if (r.getFirstName() != null) m.setFirstName(r.getFirstName());
        if (r.getLastName() != null) m.setLastName(r.getLastName());
        if (r.getMiddleName() != null) m.setMiddleName(r.getMiddleName());
        if (r.getPhone() != null) m.setPhone(r.getPhone());
        if (r.getCity() != null) m.setCity(r.getCity());
        if (r.getCountry() != null) m.setCountry(r.getCountry());
        if (r.getAbout() != null) m.setAbout(r.getAbout());
        if (r.getExperienceYears() != null) m.setExperienceYears(r.getExperienceYears());
    }
}