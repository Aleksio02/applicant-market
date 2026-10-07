package com.maboy.applicantmarket.applicant.service;

import com.maboy.applicantmarket.applicant.dao.ApplicantProfileDao;
import com.maboy.applicantmarket.applicant.dao.FspAchievementStubDao;
import com.maboy.applicantmarket.applicant.dao.dto.ApplicantProfileDto;
import com.maboy.applicantmarket.applicant.dao.dto.FspAchievementStubDto;
import com.maboy.applicantmarket.applicant.model.FspAchievementStub;
import com.maboy.applicantmarket.applicant.model.exception.ApplicantNotFoundException;
import com.maboy.applicantmarket.applicant.model.exception.FspIdAlreadyLinkedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FspService {

    private final ApplicantProfileDao profileDao;
    private final FspAchievementStubDao achievementDao;

    @Transactional
    public void link(UUID userId, String fspId) {
        ApplicantProfileDto profile = profileDao.findByUserId(userId)
                .orElseThrow(() -> new ApplicantNotFoundException("Profile not found"));

        profileDao.findByFspId(fspId).ifPresent(other -> {
            if (!other.getId().equals(profile.getId())) {
                throw new FspIdAlreadyLinkedException("FSP ID already linked to another applicant");
            }
        });

        profile.setFspId(fspId);
        profile.setFspLinkedAt(java.time.Instant.now());
        profileDao.save(profile);
    }

    @Transactional
    public void unlink(UUID userId) {
        ApplicantProfileDto profile = profileDao.findByUserId(userId)
                .orElseThrow(() -> new ApplicantNotFoundException("Profile not found"));
        profile.setFspId(null);
        profile.setFspLinkedAt(null);
        profileDao.save(profile);
    }

    @Transactional(readOnly = true)
    public List<FspAchievementStub> listAchievements(UUID userId) {
        UUID applicantId = profileDao.findByUserId(userId)
                .orElseThrow(() -> new ApplicantNotFoundException("Profile not found"))
                .getId();
        // Пустой список — валидный случай. Никаких исключений.
        return achievementDao.findAllByApplicantIdOrderByEventDateDesc(applicantId).stream()
                .map(this::toModel)
                .toList();
    }

    private FspAchievementStub toModel(FspAchievementStubDto e) {
        return FspAchievementStub.builder()
                .id(e.getId())
                .eventName(e.getEventName())
                .eventDate(e.getEventDate())
                .place(e.getPlace())
                .category(e.getCategory())
                .verified(Boolean.TRUE.equals(e.getVerified()))
                .source(e.getSource())
                .build();
    }
}