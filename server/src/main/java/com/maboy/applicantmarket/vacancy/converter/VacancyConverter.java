package com.maboy.applicantmarket.vacancy.converter;

import com.maboy.applicantmarket.vacancy.dao.dto.VacancyDto;
import com.maboy.applicantmarket.vacancy.model.Vacancy;

public class VacancyConverter {
    public void fromDto(VacancyDto source, Vacancy destination) {
        destination.setId(source.getId());
        destination.setCompanyId(source.getCompanyId());
        destination.setTitle(source.getTitle());
        destination.setDescription(source.getDescription());
        destination.setSpecializationId(source.getSpecializationId());
        destination.setGradeId(source.getGradeId());
        destination.setSalaryFrom(source.getSalaryFrom());
        destination.setSalaryTo(source.getSalaryTo());
        destination.setFormat(source.getFormat());
        destination.setLocation(source.getLocation());
        destination.setStatus(source.getStatus());
        destination.setPublishedAt(source.getPublishedAt());
        destination.setClosedAt(source.getClosedAt());
        destination.setCreatedAt(source.getCreatedAt());
        destination.setUpdatedAt(source.getUpdatedAt());
    }
}