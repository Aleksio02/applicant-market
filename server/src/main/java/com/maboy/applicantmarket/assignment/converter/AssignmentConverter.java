package com.maboy.applicantmarket.assignment.converter;

import com.maboy.applicantmarket.assignment.dao.dto.VacancyAssignmentDto;
import com.maboy.applicantmarket.assignment.model.VacancyAssignment;

public class AssignmentConverter {
    public void fromDto(VacancyAssignmentDto source, VacancyAssignment destination) {
        destination.setId(source.getId());
        destination.setVacancyId(source.getVacancyId());
        destination.setTitle(source.getTitle());
        destination.setDescription(source.getDescription());
        destination.setDurationHours(source.getDurationHours());
        destination.setActive(source.isActive());
        destination.setCreatedAt(source.getCreatedAt());
        destination.setUpdatedAt(source.getUpdatedAt());
    }
}