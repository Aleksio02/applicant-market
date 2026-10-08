package com.maboy.applicantmarket.interaction.service;

import com.maboy.applicantmarket.interaction.api.InteractionModuleApi;
import com.maboy.applicantmarket.interaction.api.model.InteractionRef;
import com.maboy.applicantmarket.interaction.dao.InteractionDao;
import com.maboy.applicantmarket.interaction.dao.dto.InteractionDto;
import com.maboy.applicantmarket.interaction.model.enums.InteractionStatus;
import com.maboy.applicantmarket.interaction.model.enums.InteractionType;
import com.maboy.applicantmarket.vacancy.model.Vacancy;
import com.maboy.applicantmarket.vacancy.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InteractionModuleApiImpl implements InteractionModuleApi {

    private static final List<InteractionStatus> ACTIVE_STATUSES =
            List.of(InteractionStatus.SENT, InteractionStatus.VIEWED);

    private final InteractionDao interactionDao;
    private final VacancyService vacancyService;

    @Override
    @Transactional
    public Optional<InteractionRef> createApplicationFromAssignment(UUID candidateId, UUID vacancyId) {
        boolean exists = interactionDao.existsByCandidateIdAndVacancyIdAndTypeAndStatusIn(
                candidateId, vacancyId, InteractionType.APPLICATION, ACTIVE_STATUSES);
        if (exists) {
            return Optional.empty();
        }

        Vacancy vacancy = vacancyService.getById(candidateId, vacancyId);

        InteractionDto dto = new InteractionDto();
        dto.setType(InteractionType.APPLICATION);
        dto.setEmployerId(vacancy.getCompanyId());
        dto.setCandidateId(candidateId);
        dto.setVacancyId(vacancyId);
        dto.setMessage("Отклик создан автоматически после успешного прохождения тестового задания.");
        dto.setSalaryFrom(vacancy.getSalaryFrom());
        dto.setSalaryTo(vacancy.getSalaryTo());
        dto.setStatus(InteractionStatus.SENT);
        dto.setContactsRevealedAt(Instant.now());

        InteractionDto saved = interactionDao.save(dto);

        return Optional.of(InteractionRef.builder()
                .id(saved.getId())
                .type(saved.getType())
                .status(saved.getStatus())
                .build());
    }

    @Override
    public boolean hasActiveInteraction(UUID candidateId, UUID vacancyId) {
        if (candidateId == null || vacancyId == null) {
            return false;
        }
        return interactionDao.existsByCandidateIdAndVacancyIdAndTypeAndStatusIn(
                candidateId, vacancyId, InteractionType.APPLICATION, ACTIVE_STATUSES);
    }
}