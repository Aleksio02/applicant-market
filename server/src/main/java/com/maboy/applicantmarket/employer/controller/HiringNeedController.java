package com.maboy.applicantmarket.employer.controller;

import com.maboy.applicantmarket.auth.config.annotation.CurrentUser;
import com.maboy.applicantmarket.commons.model.response.PageResponse;
import com.maboy.applicantmarket.employer.model.HiringNeed;
import com.maboy.applicantmarket.employer.model.request.CreateHiringNeedRequest;
import com.maboy.applicantmarket.employer.model.request.GetHiringNeedListRequest;
import com.maboy.applicantmarket.employer.model.request.UpdateHiringNeedRequest;
import com.maboy.applicantmarket.employer.service.HiringNeedService;
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
@RequestMapping("/api/employer/hiring-need")
public class HiringNeedController {

    private final HiringNeedService hiringNeedService;

    public HiringNeedController(HiringNeedService hiringNeedService) {
        this.hiringNeedService = hiringNeedService;
    }

    @PostMapping
    public HiringNeed create(@Valid @RequestBody CreateHiringNeedRequest request,
                             @CurrentUser UUID ownerId) {
        return hiringNeedService.create(ownerId, request);
    }

    @GetMapping
    public PageResponse<HiringNeed> getList(GetHiringNeedListRequest request,
                                            @CurrentUser UUID ownerId) {
        return hiringNeedService.getList(ownerId, request);
    }

    @GetMapping("/{id}")
    public HiringNeed getById(@PathVariable UUID id,
                              @CurrentUser UUID ownerId) {
        return hiringNeedService.getById(ownerId, id);
    }

    @PatchMapping("/{id}")
    public HiringNeed update(@PathVariable UUID id,
                             @Valid @RequestBody UpdateHiringNeedRequest request,
                             @CurrentUser UUID ownerId) {
        return hiringNeedService.update(ownerId, id, request);
    }

    @PatchMapping("/{id}/activate")
    public HiringNeed activate(@PathVariable UUID id,
                               @CurrentUser UUID ownerId) {
        return hiringNeedService.activate(ownerId, id);
    }

    @PatchMapping("/{id}/deactivate")
    public HiringNeed deactivate(@PathVariable UUID id,
                                 @CurrentUser UUID ownerId) {
        return hiringNeedService.deactivate(ownerId, id);
    }
}