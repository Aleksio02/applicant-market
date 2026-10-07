package com.maboy.applicantmarket.employer.converter;

import com.maboy.applicantmarket.employer.dao.dto.HiringNeedDto;
import com.maboy.applicantmarket.employer.model.HiringNeed;

public class HiringNeedConverter {
    public void fromDto(HiringNeedDto source, HiringNeed destination) {
        destination.setId(source.getId());
        if (source.getCompany() != null) {
            destination.setCompanyId(source.getCompany().getId());
        }
        destination.setTitle(source.getTitle());
        destination.setDescription(source.getDescription());
        destination.setSpecializationId(source.getSpecializationId());
        destination.setGradeId(source.getGradeId());
        destination.setSalaryFrom(source.getSalaryFrom());
        destination.setSalaryTo(source.getSalaryTo());
        destination.setFormat(source.getFormat());
        destination.setLocation(source.getLocation());
        destination.setActive(source.isActive());
        destination.setCreatedAt(source.getCreatedAt());
        destination.setUpdatedAt(source.getUpdatedAt());
    }
}