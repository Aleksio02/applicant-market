package com.maboy.applicantmarket.vacancy.service;

import com.maboy.applicantmarket.commons.model.response.PageResponse;
import com.maboy.applicantmarket.vacancy.model.Vacancy;
import com.maboy.applicantmarket.vacancy.model.request.CreateVacancyRequest;
import com.maboy.applicantmarket.vacancy.model.request.GetVacancyListRequest;
import com.maboy.applicantmarket.vacancy.model.request.UpdateVacancyRequest;

import java.util.UUID;

public interface VacancyService {
    Vacancy create(UUID ownerId, CreateVacancyRequest request);
    PageResponse<Vacancy> getListForApplicant(GetVacancyListRequest request);
    PageResponse<Vacancy> getListForEmployer(UUID ownerId, GetVacancyListRequest request);
    Vacancy getById(UUID requesterId, UUID id);
    Vacancy update(UUID ownerId, UUID id, UpdateVacancyRequest request);
    Vacancy publish(UUID ownerId, UUID id);
    Vacancy close(UUID ownerId, UUID id);
    UUID getCompanyIdByVacancyId(UUID vacancyId);
}