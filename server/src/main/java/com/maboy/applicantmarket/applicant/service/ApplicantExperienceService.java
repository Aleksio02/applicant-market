package com.maboy.applicantmarket.applicant.service;

import com.maboy.applicantmarket.applicant.converter.ApplicantExperienceConverter;
import com.maboy.applicantmarket.applicant.dao.ApplicantExperienceDao;
import com.maboy.applicantmarket.applicant.dao.dto.ApplicantExperienceDto;
import com.maboy.applicantmarket.applicant.model.ApplicantExperience;
import com.maboy.applicantmarket.applicant.model.exception.ApplicantNotFoundException;
import com.maboy.applicantmarket.applicant.model.request.AddExperienceRequest;
import com.maboy.applicantmarket.applicant.model.request.UpdateExperienceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicantExperienceService {

    private final ApplicantExperienceDao dao;
    private final ApplicantProfileService profileService;
    private final ApplicantExperienceConverter converter;

    @Transactional(readOnly = true)
    public List<ApplicantExperience> list(UUID userId) {
        UUID applicantId = profileService.getByUserId(userId).getId();
        return dao.findAllByApplicantIdOrderBySortOrderAsc(applicantId).stream()
                .map(converter::toModel)
                .toList();
    }

    @Transactional
    public ApplicantExperience add(UUID userId, AddExperienceRequest request) {
        UUID applicantId = profileService.getOrCreate(userId).getId();
        ApplicantExperience model = ApplicantExperience.builder()
                .applicantId(applicantId)
                .build();
        converter.applyAdd(model, request);
        return converter.toModel(dao.save(converter.toEntity(model)));
    }

    @Transactional
    public ApplicantExperience update(UUID userId, UUID experienceId, UpdateExperienceRequest request) {
        UUID applicantId = profileService.getByUserId(userId).getId();
        ApplicantExperienceDto entity = dao.findById(experienceId)
                .filter(e -> e.getApplicantId().equals(applicantId))
                .orElseThrow(() -> new ApplicantNotFoundException("Experience not found"));
        ApplicantExperience model = converter.toModel(entity);
        converter.applyUpdate(model, request);
        ApplicantExperienceDto updated = converter.toEntity(model);
        updated.setId(entity.getId());
        updated.setApplicantId(entity.getApplicantId());
        updated.setCreatedAt(entity.getCreatedAt());
        return converter.toModel(dao.save(updated));
    }

    @Transactional
    public void delete(UUID userId, UUID experienceId) {
        UUID applicantId = profileService.getByUserId(userId).getId();
        ApplicantExperienceDto entity = dao.findById(experienceId)
                .filter(e -> e.getApplicantId().equals(applicantId))
                .orElseThrow(() -> new ApplicantNotFoundException("Experience not found"));
        dao.delete(entity);
    }
}