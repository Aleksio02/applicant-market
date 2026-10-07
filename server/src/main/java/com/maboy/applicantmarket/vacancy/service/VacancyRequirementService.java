package com.maboy.applicantmarket.vacancy.service;

import com.maboy.applicantmarket.vacancy.model.VacancyRequirement;
import com.maboy.applicantmarket.vacancy.model.request.CreateVacancyRequirementRequest;
import com.maboy.applicantmarket.vacancy.model.request.UpdateVacancyRequirementRequest;

import java.util.List;
import java.util.UUID;

public interface VacancyRequirementService {
    VacancyRequirement create(UUID ownerId, UUID vacancyId, CreateVacancyRequirementRequest request);
    List<VacancyRequirement> getList(UUID requesterId, UUID vacancyId);
    VacancyRequirement update(UUID ownerId, UUID vacancyId, UUID requirementId, UpdateVacancyRequirementRequest request);
    void delete(UUID ownerId, UUID vacancyId, UUID requirementId);
}