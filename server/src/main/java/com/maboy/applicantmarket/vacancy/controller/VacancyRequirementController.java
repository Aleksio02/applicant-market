package com.maboy.applicantmarket.vacancy.controller;

import com.maboy.applicantmarket.auth.config.annotation.CurrentUser;
import com.maboy.applicantmarket.vacancy.model.VacancyRequirement;
import com.maboy.applicantmarket.vacancy.model.request.CreateVacancyRequirementRequest;
import com.maboy.applicantmarket.vacancy.model.request.UpdateVacancyRequirementRequest;
import com.maboy.applicantmarket.vacancy.service.VacancyRequirementService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vacancy/requirement")
public class VacancyRequirementController {

    private final VacancyRequirementService requirementService;

    public VacancyRequirementController(VacancyRequirementService requirementService) {
        this.requirementService = requirementService;
    }

    @PostMapping("/vacancy/{vacancyId}")
    public VacancyRequirement create(@PathVariable UUID vacancyId,
                                     @Valid @RequestBody CreateVacancyRequirementRequest request,
                                     @CurrentUser UUID ownerId) {
        return requirementService.create(ownerId, vacancyId, request);
    }

    @GetMapping("/vacancy/{vacancyId}")
    public List<VacancyRequirement> getList(@PathVariable UUID vacancyId,
                                            @CurrentUser UUID requesterId) {
        return requirementService.getList(requesterId, vacancyId);
    }

    @PatchMapping("/{requirementId}/vacancy/{vacancyId}")
    public VacancyRequirement update(@PathVariable UUID vacancyId,
                                     @PathVariable UUID requirementId,
                                     @Valid @RequestBody UpdateVacancyRequirementRequest request,
                                     @CurrentUser UUID ownerId) {
        return requirementService.update(ownerId, vacancyId, requirementId, request);
    }

    @DeleteMapping("/{requirementId}/vacancy/{vacancyId}")
    public void delete(@PathVariable UUID vacancyId,
                       @PathVariable UUID requirementId,
                       @CurrentUser UUID ownerId) {
        requirementService.delete(ownerId, vacancyId, requirementId);
    }
}