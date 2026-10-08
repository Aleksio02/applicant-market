package com.maboy.applicantmarket.commons.controller;

import com.maboy.applicantmarket.commons.model.Grade;
import com.maboy.applicantmarket.commons.model.Skill;
import com.maboy.applicantmarket.commons.model.Specialization;
import com.maboy.applicantmarket.commons.service.CatalogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/specializations")
    public List<Specialization> getSpecializations() {
        return catalogService.getSpecializations();
    }

    @GetMapping("/grades")
    public List<Grade> getGrades() {
        return catalogService.getGrades();
    }

    @GetMapping("/skills")
    public List<Skill> getSkills() {
        return catalogService.getSkills();
    }
}