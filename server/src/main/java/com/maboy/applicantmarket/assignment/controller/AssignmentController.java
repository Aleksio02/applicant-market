package com.maboy.applicantmarket.assignment.controller;

import com.maboy.applicantmarket.assignment.model.VacancyAssignment;
import com.maboy.applicantmarket.assignment.model.request.CreateAssignmentRequest;
import com.maboy.applicantmarket.assignment.model.request.UpdateAssignmentRequest;
import com.maboy.applicantmarket.assignment.service.AssignmentService;
import com.maboy.applicantmarket.auth.config.annotation.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/assignment")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping("/vacancy/{vacancyId}")
    public VacancyAssignment create(@PathVariable UUID vacancyId,
                                    @Valid @RequestBody CreateAssignmentRequest request,
                                    @CurrentUser UUID ownerId) {
        return assignmentService.create(ownerId, vacancyId, request);
    }

    @GetMapping("/vacancy/{vacancyId}")
    public VacancyAssignment getByVacancy(@PathVariable UUID vacancyId) {
        return assignmentService.getByVacancy(vacancyId);
    }

    @PatchMapping("/vacancy/{vacancyId}")
    public VacancyAssignment update(@PathVariable UUID vacancyId,
                                    @Valid @RequestBody UpdateAssignmentRequest request,
                                    @CurrentUser UUID ownerId) {
        return assignmentService.update(ownerId, vacancyId, request);
    }

    @DeleteMapping("/vacancy/{vacancyId}")
    public void delete(@PathVariable UUID vacancyId,
                       @CurrentUser UUID ownerId) {
        assignmentService.delete(ownerId, vacancyId);
    }
}