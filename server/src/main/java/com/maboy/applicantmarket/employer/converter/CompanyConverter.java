package com.maboy.applicantmarket.employer.converter;

import com.maboy.applicantmarket.employer.dao.dto.CompanyDto;
import com.maboy.applicantmarket.employer.model.Company;

public class CompanyConverter {
    public void fromDto(CompanyDto source, Company destination) {
        destination.setId(source.getId());
        destination.setOwnerId(source.getOwnerId());
        destination.setName(source.getName());
        destination.setDescription(source.getDescription());
        destination.setIndustry(source.getIndustry());
        destination.setWebsite(source.getWebsite());
        destination.setContactPersonName(source.getContactPersonName());
        destination.setContactPersonPosition(source.getContactPersonPosition());
        destination.setContactEmail(source.getContactEmail());
        destination.setContactPhone(source.getContactPhone());
        destination.setLogoUrl(source.getLogoUrl());
        destination.setCreatedAt(source.getCreatedAt());
        destination.setUpdatedAt(source.getUpdatedAt());
    }
}