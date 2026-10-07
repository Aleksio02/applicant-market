package com.maboy.applicantmarket.employer.service;

import com.maboy.applicantmarket.auth.service.AuthService;
import com.maboy.applicantmarket.commons.exception.AlreadyExistsException;
import com.maboy.applicantmarket.commons.exception.NotFoundException;
import com.maboy.applicantmarket.employer.converter.CompanyConverter;
import com.maboy.applicantmarket.employer.dao.CompanyDao;
import com.maboy.applicantmarket.employer.dao.dto.CompanyDto;
import com.maboy.applicantmarket.employer.model.Company;
import com.maboy.applicantmarket.employer.model.request.CreateCompanyRequest;
import com.maboy.applicantmarket.employer.model.request.UpdateCompanyRequest;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Primary
@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyDao companyDao;
    private final AuthService authService;

    public CompanyServiceImpl(CompanyDao companyDao, AuthService authService) {
        this.companyDao = companyDao;
        this.authService = authService;
    }

    @Override
    @Transactional
    public Company create(UUID ownerId, CreateCompanyRequest request) {
        authService.requireEmployer(ownerId);

        if (companyDao.existsByOwnerId(ownerId)) {
            throw new AlreadyExistsException("Company for this user already exists");
        }

        CompanyDto dto = new CompanyDto();
        dto.setOwnerId(ownerId);
        dto.setName(request.getName());
        dto.setDescription(request.getDescription());
        dto.setIndustry(request.getIndustry());
        dto.setWebsite(request.getWebsite());
        dto.setContactPersonName(request.getContactPersonName());
        dto.setContactPersonPosition(request.getContactPersonPosition());
        dto.setContactEmail(request.getContactEmail());
        dto.setContactPhone(request.getContactPhone());
        dto.setLogoUrl(request.getLogoUrl());
        Instant now = Instant.now();
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);

        CompanyDto saved = companyDao.save(dto);

        Company company = new Company();
        new CompanyConverter().fromDto(saved, company);
        return company;
    }

    @Override
    public Company getMine(UUID ownerId) {
        authService.requireEmployer(ownerId);

        CompanyDto dto = companyDao.findByOwnerId(ownerId)
                .orElseThrow(() -> new NotFoundException("Company not found"));

        Company company = new Company();
        new CompanyConverter().fromDto(dto, company);
        return company;
    }

    @Override
    @Transactional
    public Company update(UUID ownerId, UpdateCompanyRequest request) {
        authService.requireEmployer(ownerId);

        CompanyDto dto = companyDao.findByOwnerId(ownerId)
                .orElseThrow(() -> new NotFoundException("Company not found"));

        request.getName().ifPresent(dto::setName);
        request.getDescription().ifPresent(dto::setDescription);
        request.getIndustry().ifPresent(dto::setIndustry);
        request.getWebsite().ifPresent(dto::setWebsite);
        request.getContactPersonName().ifPresent(dto::setContactPersonName);
        request.getContactPersonPosition().ifPresent(dto::setContactPersonPosition);
        request.getContactEmail().ifPresent(dto::setContactEmail);
        request.getContactPhone().ifPresent(dto::setContactPhone);
        request.getLogoUrl().ifPresent(dto::setLogoUrl);
        dto.setUpdatedAt(Instant.now());

        CompanyDto saved = companyDao.save(dto);

        Company company = new Company();
        new CompanyConverter().fromDto(saved, company);
        return company;
    }
}