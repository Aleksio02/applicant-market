package com.maboy.applicantmarket.employer.service;

import com.maboy.applicantmarket.commons.model.response.PageResponse;
import com.maboy.applicantmarket.employer.model.HiringNeed;
import com.maboy.applicantmarket.employer.model.request.CreateHiringNeedRequest;
import com.maboy.applicantmarket.employer.model.request.GetHiringNeedListRequest;
import com.maboy.applicantmarket.employer.model.request.UpdateHiringNeedRequest;

import java.util.UUID;

public interface HiringNeedService {
    HiringNeed create(UUID ownerId, CreateHiringNeedRequest request);
    PageResponse<HiringNeed> getList(UUID ownerId, GetHiringNeedListRequest request);
    HiringNeed getById(UUID ownerId, UUID id);
    HiringNeed update(UUID ownerId, UUID id, UpdateHiringNeedRequest request);
    HiringNeed activate(UUID ownerId, UUID id);
    HiringNeed deactivate(UUID ownerId, UUID id);
}