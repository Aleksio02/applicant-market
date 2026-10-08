package com.maboy.applicantmarket.vacancy.controller;

import com.maboy.applicantmarket.auth.config.annotation.CurrentUser;
import com.maboy.applicantmarket.commons.model.response.PageResponse;
import com.maboy.applicantmarket.vacancy.model.Vacancy;
import com.maboy.applicantmarket.vacancy.model.request.CreateVacancyRequest;
import com.maboy.applicantmarket.vacancy.model.request.GetVacancyListRequest;
import com.maboy.applicantmarket.vacancy.model.request.UpdateVacancyRequest;
import com.maboy.applicantmarket.vacancy.service.VacancyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/vacancy")
public class VacancyController {

    private final VacancyService vacancyService;

    public VacancyController(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    @PostMapping
    public Vacancy create(@Valid @RequestBody CreateVacancyRequest request,
                          @CurrentUser UUID ownerId) {
        return vacancyService.create(ownerId, request);
    }

    @GetMapping
    public PageResponse<Vacancy> getPublished(GetVacancyListRequest request) {
        return vacancyService.getListForApplicant(request);
    }

    @GetMapping("/mine")
    public PageResponse<Vacancy> getMine(GetVacancyListRequest request,
                                         @CurrentUser UUID ownerId) {
        return vacancyService.getListForEmployer(ownerId, request);
    }

    @GetMapping("/{id}")
    public Vacancy getById(@PathVariable UUID id,
                           @CurrentUser UUID requesterId) {
        return vacancyService.getById(requesterId, id);
    }

    @PatchMapping("/{id}")
    public Vacancy update(@PathVariable UUID id,
                          @Valid @RequestBody UpdateVacancyRequest request,
                          @CurrentUser UUID ownerId) {
        return vacancyService.update(ownerId, id, request);
    }

    @PatchMapping("/{id}/publish")
    public Vacancy publish(@PathVariable UUID id,
                           @CurrentUser UUID ownerId) {
        return vacancyService.publish(ownerId, id);
    }

    @PatchMapping("/{id}/close")
    public Vacancy close(@PathVariable UUID id,
                         @CurrentUser UUID ownerId) {
        return vacancyService.close(ownerId, id);
    }
}