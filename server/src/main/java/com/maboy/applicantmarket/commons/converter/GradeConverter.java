package com.maboy.applicantmarket.commons.converter;

import com.maboy.applicantmarket.commons.dao.dto.GradeDto;
import com.maboy.applicantmarket.commons.model.Grade;

public class GradeConverter {
    public void fromDto(GradeDto source, Grade destination) {
        destination.setId(source.getId());
        destination.setCode(source.getCode());
        destination.setName(source.getName());
        destination.setLevel(source.getLevel());
        destination.setDescription(source.getDescription());
    }
}