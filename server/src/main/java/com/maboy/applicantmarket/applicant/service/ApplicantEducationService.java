package com.maboy.applicantmarket.applicant.service;

import com.maboy.applicantmarket.applicant.converter.ApplicantEducationConverter;
import com.maboy.applicantmarket.applicant.dao.ApplicantEducationDao;
import com.maboy.applicantmarket.applicant.dao.dto.ApplicantEducationDto;
import com.maboy.applicantmarket.applicant.model.ApplicantEducation;
import com.maboy.applicantmarket.applicant.model.exception.ApplicantNotFoundException;
import com.maboy.applicantmarket.applicant.model.request.AddEducationRequest;
import com.maboy.applicantmarket.applicant.model.request.UpdateEducationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicantEducationService {

    private final ApplicantEducationDao dao;
    private final ApplicantProfileService profileService;
    private final ApplicantEducationConverter converter;

    @Transactional(readOnly = true)
    public List<ApplicantEducation> list(UUID userId) {
        UUID applicantId = profileService.getByUserId(userId).getId();
        return dao.findAllByApplicantId(applicantId).stream()
            .map(converter::toModel)
            .toList();
    }

    @Transactional
    public ApplicantEducation add(UUID userId, AddEducationRequest request) {
        UUID applicantId = profileService.getOrCreate(userId).getId();
        ApplicantEducation model = ApplicantEducation.builder()
            .applicantId(applicantId)
            .build();
        converter.applyAdd(model, request);
        return converter.toModel(dao.save(converter.toEntity(model)));
    }

    @Transactional
    public ApplicantEducation update(UUID userId, UUID educationId, UpdateEducationRequest request) {
        UUID applicantId = profileService.getByUserId(userId).getId();
        ApplicantEducationDto entity = dao.findById(educationId)
            .filter(e -> e.getApplicantId().equals(applicantId))
            .orElseThrow(() -> new ApplicantNotFoundException("Education not found"));
        ApplicantEducation model = converter.toModel(entity);
        converter.applyUpdate(model, request);
        ApplicantEducationDto updated = converter.toEntity(model);
        updated.setId(entity.getId());
        updated.setApplicantId(entity.getApplicantId());
        updated.setCreatedAt(entity.getCreatedAt());
        return converter.toModel(dao.save(updated));
    }

    @Transactional
    public void delete(UUID userId, UUID educationId) {
        UUID applicantId = profileService.getByUserId(userId).getId();
        ApplicantEducationDto entity = dao.findById(educationId)
            .filter(e -> e.getApplicantId().equals(applicantId))
            .orElseThrow(() -> new ApplicantNotFoundException("Education not found"));
        dao.delete(entity);
    }
}