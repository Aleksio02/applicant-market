package com.maboy.applicantmarket.commons.service;

import com.maboy.applicantmarket.commons.model.Grade;
import com.maboy.applicantmarket.commons.model.Skill;
import com.maboy.applicantmarket.commons.model.Specialization;

import java.util.List;

public interface CatalogService {
    List<Specialization> getSpecializations();
    List<Grade> getGrades();
    List<Skill> getSkills();
}