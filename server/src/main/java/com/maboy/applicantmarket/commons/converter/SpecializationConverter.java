package com.maboy.applicantmarket.commons.converter;

import com.maboy.applicantmarket.commons.dao.dto.SpecializationDto;
import com.maboy.applicantmarket.commons.model.Specialization;

public class SpecializationConverter {
    public void fromDto(SpecializationDto source, Specialization destination) {
        destination.setId(source.getId());
        destination.setCode(source.getCode());
        destination.setName(source.getName());
        destination.setDescription(source.getDescription());
    }
}