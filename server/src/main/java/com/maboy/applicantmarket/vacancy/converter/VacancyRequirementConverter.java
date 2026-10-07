package com.maboy.applicantmarket.vacancy.converter;

import com.maboy.applicantmarket.vacancy.dao.dto.VacancyRequirementDto;
import com.maboy.applicantmarket.vacancy.model.VacancyRequirement;

public class VacancyRequirementConverter {
    public void fromDto(VacancyRequirementDto source, VacancyRequirement destination) {
        destination.setId(source.getId());
        if (source.getVacancy() != null) {
            destination.setVacancyId(source.getVacancy().getId());
        }
        destination.setSkillId(source.getSkillId());
        destination.setLevel(source.getLevel());
        destination.setMandatory(source.isMandatory());
        destination.setCreatedAt(source.getCreatedAt());
        destination.setUpdatedAt(source.getUpdatedAt());
    }
}