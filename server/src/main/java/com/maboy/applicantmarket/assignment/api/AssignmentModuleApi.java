package com.maboy.applicantmarket.assignment.api;

import java.util.Optional;
import java.util.UUID;

public interface AssignmentModuleApi {
    boolean hasPassedAssignment(UUID candidateId, UUID vacancyId);
    Optional<UUID> findAssignmentIdByVacancy(UUID vacancyId);
}