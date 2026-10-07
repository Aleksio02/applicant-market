package com.maboy.applicantmarket.employer.service;

import com.maboy.applicantmarket.employer.model.Company;
import com.maboy.applicantmarket.employer.model.request.CreateCompanyRequest;
import com.maboy.applicantmarket.employer.model.request.UpdateCompanyRequest;

import java.util.UUID;

public interface CompanyService {
    Company create(UUID ownerId, CreateCompanyRequest request);
    Company getMine(UUID ownerId);
    Company update(UUID ownerId, UpdateCompanyRequest request);
}