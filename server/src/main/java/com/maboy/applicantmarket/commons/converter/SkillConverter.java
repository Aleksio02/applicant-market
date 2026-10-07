package com.maboy.applicantmarket.commons.converter;

import com.maboy.applicantmarket.commons.dao.dto.SkillDto;
import com.maboy.applicantmarket.commons.model.Skill;

public class SkillConverter {
    public void fromDto(SkillDto source, Skill destination) {
        destination.setId(source.getId());
        destination.setCode(source.getCode());
        destination.setName(source.getName());
        destination.setCategory(source.getCategory());
    }
}