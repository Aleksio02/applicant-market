package com.maboy.applicantmarket.assignment.service;

import com.maboy.applicantmarket.assignment.converter.AssignmentAttemptConverter;
import com.maboy.applicantmarket.assignment.dao.AssignmentAttemptDao;
import com.maboy.applicantmarket.assignment.dao.AssignmentDao;
import com.maboy.applicantmarket.assignment.dao.dto.AssignmentAttemptDto;
import com.maboy.applicantmarket.assignment.dao.dto.VacancyAssignmentDto;
import com.maboy.applicantmarket.assignment.model.AssignmentAttempt;
import com.maboy.applicantmarket.assignment.model.enums.AssignmentAttemptStatus;
import com.maboy.applicantmarket.assignment.model.enums.EvaluationVerdict;
import com.maboy.applicantmarket.assignment.model.request.EvaluateAttemptRequest;
import com.maboy.applicantmarket.assignment.model.request.SubmitAttemptRequest;
import com.maboy.applicantmarket.auth.service.AuthService;
import com.maboy.applicantmarket.commons.exception.AccessForbiddenException;
import com.maboy.applicantmarket.commons.exception.AlreadyExistsException;
import com.maboy.applicantmarket.commons.exception.IncorrectRequestDataException;
import com.maboy.applicantmarket.commons.exception.NotFoundException;
import com.maboy.applicantmarket.employer.service.CompanyService;
import com.maboy.applicantmarket.interaction.api.InteractionModuleApi;
import com.maboy.applicantmarket.vacancy.service.VacancyService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Primary
@Service
public class AssignmentAttemptServiceImpl implements AssignmentAttemptService {

    private final AssignmentAttemptDao attemptDao;
    private final AssignmentDao assignmentDao;
    private final CompanyService companyService;
    private final VacancyService vacancyService;
    private final AuthService authService;
    private final InteractionModuleApi interactionModuleApi;

    public AssignmentAttemptServiceImpl(AssignmentAttemptDao attemptDao,
                                        AssignmentDao assignmentDao,
                                        CompanyService companyService,
                                        VacancyService vacancyService,
                                        AuthService authService,
                                        InteractionModuleApi interactionModuleApi) {
        this.attemptDao = attemptDao;
        this.assignmentDao = assignmentDao;
        this.companyService = companyService;
        this.vacancyService = vacancyService;
        this.authService = authService;
        this.interactionModuleApi = interactionModuleApi;
    }

    @Override
    @Transactional
    public AssignmentAttempt start(UUID candidateId, UUID assignmentId) {
        authService.requireApplicant(candidateId);

        VacancyAssignmentDto assignment = assignmentDao.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found"));

        if (!assignment.isActive()) {
            throw new IncorrectRequestDataException("Assignment is not active");
        }

        if (attemptDao.existsByAssignmentIdAndCandidateId(assignmentId, candidateId)) {
            throw new AlreadyExistsException("Attempt already exists for this assignment");
        }

        Instant now = Instant.now();
        AssignmentAttemptDto dto = new AssignmentAttemptDto();
        dto.setAssignment(assignment);
        dto.setCandidateId(candidateId);
        dto.setStatus(AssignmentAttemptStatus.STARTED);
        dto.setStartedAt(now);
        dto.setDeadlineAt(now.plus(assignment.getDurationHours(), ChronoUnit.HOURS));
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);

        AssignmentAttemptDto saved = attemptDao.save(dto);
        return toModel(saved);
    }

    @Override
    @Transactional
    public AssignmentAttempt submit(UUID candidateId, UUID attemptId, SubmitAttemptRequest request) {
        AssignmentAttemptDto dto = attemptDao.findById(attemptId)
                .orElseThrow(() -> new NotFoundException("Attempt not found"));

        if (!dto.getCandidateId().equals(candidateId)) {
            throw new AccessForbiddenException("Attempt belongs to another candidate");
        }

        if (dto.getStatus() != AssignmentAttemptStatus.STARTED) {
            throw new IncorrectRequestDataException("Attempt is not in STARTED state");
        }

        Instant now = Instant.now();
        if (now.isAfter(dto.getDeadlineAt())) {
            throw new IncorrectRequestDataException("Attempt deadline expired");
        }

        dto.setStatus(AssignmentAttemptStatus.SUBMITTED);
        dto.setSubmittedAt(now);
        dto.setContentText(request.getContentText());
        dto.setUpdatedAt(now);

        AssignmentAttemptDto saved = attemptDao.save(dto);
        return toModel(saved);
    }

    @Override
    @Transactional
    public AssignmentAttempt getById(UUID requesterId, UUID attemptId) {
        AssignmentAttemptDto dto = attemptDao.findById(attemptId)
                .orElseThrow(() -> new NotFoundException("Attempt not found"));

        refreshExpiredStatus(dto);

        boolean isCandidate = dto.getCandidateId().equals(requesterId);
        boolean isOwner = isOwnerOfAssignment(dto, requesterId);

        if (!isCandidate && !isOwner) {
            throw new AccessForbiddenException("You do not have access to this attempt");
        }

        return toModel(dto);
    }

    @Override
    @Transactional
    public List<AssignmentAttempt> getMine(UUID candidateId) {
        authService.requireApplicant(candidateId);
        List<AssignmentAttemptDto> found = attemptDao.findAllByCandidateIdOrderByCreatedAtDesc(candidateId);
        List<AssignmentAttempt> result = new ArrayList<>(found.size());
        for (AssignmentAttemptDto dto : found) {
            refreshExpiredStatus(dto);
            result.add(toModel(dto));
        }
        return result;
    }

    @Override
    @Transactional
    public List<AssignmentAttempt> getByAssignment(UUID ownerId, UUID assignmentId) {
        authService.requireEmployer(ownerId);

        VacancyAssignmentDto assignment = assignmentDao.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found"));
        requireOwnedByRequester(assignment.getVacancyId(), ownerId);

        List<AssignmentAttemptDto> found = attemptDao.findAllByAssignmentIdOrderByCreatedAtDesc(assignmentId);
        List<AssignmentAttempt> result = new ArrayList<>(found.size());
        for (AssignmentAttemptDto dto : found) {
            refreshExpiredStatus(dto);
            result.add(toModel(dto));
        }
        return result;
    }

    @Override
    @Transactional
    public AssignmentAttempt evaluate(UUID ownerId, UUID attemptId, EvaluateAttemptRequest request) {
        authService.requireEmployer(ownerId);

        AssignmentAttemptDto dto = attemptDao.findById(attemptId)
                .orElseThrow(() -> new NotFoundException("Attempt not found"));

        requireOwnedByRequester(dto.getAssignment().getVacancyId(), ownerId);

        if (dto.getStatus() != AssignmentAttemptStatus.SUBMITTED) {
            throw new IncorrectRequestDataException("Attempt is not in SUBMITTED state");
        }

        Instant now = Instant.now();
        dto.setStatus(AssignmentAttemptStatus.EVALUATED);
        dto.setVerdict(request.getVerdict());
        dto.setScore(request.getScore());
        dto.setFeedback(request.getFeedback());
        dto.setEvaluatedAt(now);
        dto.setUpdatedAt(now);

        AssignmentAttemptDto saved = attemptDao.save(dto);

        // Если задание пройдено — создаём отклик от кандидата на вакансию.
        // Метод идемпотентен: если активный отклик уже есть, новый не создаётся.
        if (saved.getVerdict() == EvaluationVerdict.PASS) {
            interactionModuleApi.createApplicationFromAssignment(
                    saved.getCandidateId(),
                    saved.getAssignment().getVacancyId()
            );
        }

        return toModel(saved);
    }

    private void requireOwnedByRequester(UUID vacancyId, UUID requesterId) {
        UUID companyId;
        try {
            companyId = companyService.getCompanyIdByOwner(requesterId);
        } catch (NotFoundException e) {
            throw new AccessForbiddenException("You do not have access to this attempt");
        }
        UUID vacancyCompanyId = vacancyService.getCompanyIdByVacancyId(vacancyId);
        if (!companyId.equals(vacancyCompanyId)) {
            throw new AccessForbiddenException("Vacancy belongs to another company");
        }
    }

    private boolean isOwnerOfAssignment(AssignmentAttemptDto dto, UUID requesterId) {
        try {
            UUID companyId = companyService.getCompanyIdByOwner(requesterId);
            UUID vacancyCompanyId = vacancyService.getCompanyIdByVacancyId(dto.getAssignment().getVacancyId());
            return companyId.equals(vacancyCompanyId);
        } catch (Exception e) {
            return false;
        }
    }

    private void refreshExpiredStatus(AssignmentAttemptDto dto) {
        if (dto.getStatus() == AssignmentAttemptStatus.STARTED
                && Instant.now().isAfter(dto.getDeadlineAt())) {
            dto.setStatus(AssignmentAttemptStatus.EXPIRED);
            dto.setUpdatedAt(Instant.now());
            attemptDao.save(dto);
        }
    }

    private AssignmentAttempt toModel(AssignmentAttemptDto dto) {
        AssignmentAttempt model = new AssignmentAttempt();
        new AssignmentAttemptConverter().fromDto(dto, model);
        return model;
    }
}