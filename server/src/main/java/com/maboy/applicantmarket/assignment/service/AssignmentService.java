package com.maboy.applicantmarket.assignment.service;

import com.maboy.applicantmarket.assignment.model.VacancyAssignment;
import com.maboy.applicantmarket.assignment.model.request.CreateAssignmentRequest;
import com.maboy.applicantmarket.assignment.model.request.UpdateAssignmentRequest;

import java.util.UUID;

public interface AssignmentService {
    VacancyAssignment create(UUID ownerId, UUID vacancyId, CreateAssignmentRequest request);
    VacancyAssignment getByVacancy(UUID vacancyId);
    VacancyAssignment update(UUID ownerId, UUID vacancyId, UpdateAssignmentRequest request);
    void delete(UUID ownerId, UUID vacancyId);
}