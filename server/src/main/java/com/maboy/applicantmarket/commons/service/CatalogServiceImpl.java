package com.maboy.applicantmarket.commons.service;

import com.maboy.applicantmarket.commons.converter.GradeConverter;
import com.maboy.applicantmarket.commons.converter.SkillConverter;
import com.maboy.applicantmarket.commons.converter.SpecializationConverter;
import com.maboy.applicantmarket.commons.dao.GradeDao;
import com.maboy.applicantmarket.commons.dao.SkillDao;
import com.maboy.applicantmarket.commons.dao.SpecializationDao;
import com.maboy.applicantmarket.commons.dao.dto.GradeDto;
import com.maboy.applicantmarket.commons.dao.dto.SkillDto;
import com.maboy.applicantmarket.commons.dao.dto.SpecializationDto;
import com.maboy.applicantmarket.commons.model.Grade;
import com.maboy.applicantmarket.commons.model.Skill;
import com.maboy.applicantmarket.commons.model.Specialization;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Primary
@Service
public class CatalogServiceImpl implements CatalogService {

    private final SpecializationDao specializationDao;
    private final GradeDao gradeDao;
    private final SkillDao skillDao;

    public CatalogServiceImpl(SpecializationDao specializationDao,
                              GradeDao gradeDao,
                              SkillDao skillDao) {
        this.specializationDao = specializationDao;
        this.gradeDao = gradeDao;
        this.skillDao = skillDao;
    }

    @Override
    public List<Specialization> getSpecializations() {
        List<SpecializationDto> found = specializationDao.findAllByOrderByNameAsc();
        List<Specialization> result = new ArrayList<>(found.size());
        SpecializationConverter converter = new SpecializationConverter();
        for (SpecializationDto dto : found) {
            Specialization model = new Specialization();
            converter.fromDto(dto, model);
            result.add(model);
        }
        return result;
    }

    @Override
    public List<Grade> getGrades() {
        List<GradeDto> found = gradeDao.findAllByOrderByLevelAsc();
        List<Grade> result = new ArrayList<>(found.size());
        GradeConverter converter = new GradeConverter();
        for (GradeDto dto : found) {
            Grade model = new Grade();
            converter.fromDto(dto, model);
            result.add(model);
        }
        return result;
    }

    @Override
    public List<Skill> getSkills() {
        List<SkillDto> found = skillDao.findAllByIsActiveTrueOrderBySortOrderAsc();
        List<Skill> result = new ArrayList<>(found.size());
        SkillConverter converter = new SkillConverter();
        for (SkillDto dto : found) {
            Skill model = new Skill();
            converter.fromDto(dto, model);
            result.add(model);
        }
        return result;
    }
}