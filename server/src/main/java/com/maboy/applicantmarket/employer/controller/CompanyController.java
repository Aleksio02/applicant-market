package com.maboy.applicantmarket.employer.controller;

import com.maboy.applicantmarket.auth.config.annotation.CurrentUser;
import com.maboy.applicantmarket.employer.model.Company;
import com.maboy.applicantmarket.employer.model.request.CreateCompanyRequest;
import com.maboy.applicantmarket.employer.model.request.UpdateCompanyRequest;
import com.maboy.applicantmarket.employer.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/employer/company")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    public Company create(@Valid @RequestBody CreateCompanyRequest request,
                          @CurrentUser UUID ownerId) {
        return companyService.create(ownerId, request);
    }

    @GetMapping("/me")
    public Company getMine(@CurrentUser UUID ownerId) {
        return companyService.getMine(ownerId);
    }

    @PatchMapping("/me")
    public Company update(@Valid @RequestBody UpdateCompanyRequest request,
                          @CurrentUser UUID ownerId) {
        return companyService.update(ownerId, request);
    }
}