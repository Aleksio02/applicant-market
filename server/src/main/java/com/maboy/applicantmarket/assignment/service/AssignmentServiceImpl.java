package com.maboy.applicantmarket.assignment.service;

import com.maboy.applicantmarket.assignment.converter.AssignmentConverter;
import com.maboy.applicantmarket.assignment.dao.AssignmentDao;
import com.maboy.applicantmarket.assignment.dao.dto.VacancyAssignmentDto;
import com.maboy.applicantmarket.assignment.model.VacancyAssignment;
import com.maboy.applicantmarket.assignment.model.request.CreateAssignmentRequest;
import com.maboy.applicantmarket.assignment.model.request.UpdateAssignmentRequest;
import com.maboy.applicantmarket.auth.service.AuthService;
import com.maboy.applicantmarket.commons.exception.AccessForbiddenException;
import com.maboy.applicantmarket.commons.exception.AlreadyExistsException;
import com.maboy.applicantmarket.commons.exception.NotFoundException;
import com.maboy.applicantmarket.employer.service.CompanyService;
import com.maboy.applicantmarket.vacancy.service.VacancyService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Primary
@Service
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentDao assignmentDao;
    private final VacancyService vacancyService;
    private final CompanyService companyService;
    private final AuthService authService;

    public AssignmentServiceImpl(AssignmentDao assignmentDao,
                                 VacancyService vacancyService,
                                 CompanyService companyService,
                                 AuthService authService) {
        this.assignmentDao = assignmentDao;
        this.vacancyService = vacancyService;
        this.companyService = companyService;
        this.authService = authService;
    }

    @Override
    @Transactional
    public VacancyAssignment create(UUID ownerId, UUID vacancyId, CreateAssignmentRequest request) {
        authService.requireEmployer(ownerId);
        UUID companyId = companyService.getCompanyIdByOwner(ownerId);
        UUID vacancyCompanyId = vacancyService.getCompanyIdByVacancyId(vacancyId);
        if (!companyId.equals(vacancyCompanyId)) {
            throw new AccessForbiddenException("Vacancy belongs to another company");
        }

        if (assignmentDao.existsByVacancyId(vacancyId)) {
            throw new AlreadyExistsException("Vacancy already has an assignment");
        }

        VacancyAssignmentDto dto = new VacancyAssignmentDto();
        dto.setVacancyId(vacancyId);
        dto.setTitle(request.getTitle());
        dto.setDescription(request.getDescription());
        dto.setDurationHours(request.getDurationHours());
        dto.setActive(true);
        Instant now = Instant.now();
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);

        VacancyAssignmentDto saved = assignmentDao.save(dto);
        return toModel(saved);
    }

    @Override
    public VacancyAssignment getByVacancy(UUID requesterId, UUID vacancyId) {
        // Проверяет, что вакансия существует и доступна запрашивающему:
        // PUBLISHED — доступна всем, DRAFT/CLOSED — только владельцу.
        vacancyService.getById(requesterId, vacancyId);

        VacancyAssignmentDto dto = assignmentDao.findByVacancyId(vacancyId)
                .orElseThrow(() -> new NotFoundException("Assignment not found"));
        return toModel(dto);
    }

    @Override
    @Transactional
    public VacancyAssignment update(UUID ownerId, UUID vacancyId, UpdateAssignmentRequest request) {
        authService.requireEmployer(ownerId);
        UUID companyId = companyService.getCompanyIdByOwner(ownerId);
        UUID vacancyCompanyId = vacancyService.getCompanyIdByVacancyId(vacancyId);
        if (!companyId.equals(vacancyCompanyId)) {
            throw new AccessForbiddenException("Vacancy belongs to another company");
        }

        VacancyAssignmentDto dto = assignmentDao.findByVacancyId(vacancyId)
                .orElseThrow(() -> new NotFoundException("Assignment not found"));

        request.getTitle().ifPresent(dto::setTitle);
        request.getDescription().ifPresent(dto::setDescription);
        request.getDurationHours().ifPresent(dto::setDurationHours);
        request.getActive().ifPresent(dto::setActive);
        dto.setUpdatedAt(Instant.now());

        VacancyAssignmentDto saved = assignmentDao.save(dto);
        return toModel(saved);
    }

    @Override
    @Transactional
    public void delete(UUID ownerId, UUID vacancyId) {
        authService.requireEmployer(ownerId);
        UUID companyId = companyService.getCompanyIdByOwner(ownerId);
        UUID vacancyCompanyId = vacancyService.getCompanyIdByVacancyId(vacancyId);
        if (!companyId.equals(vacancyCompanyId)) {
            throw new AccessForbiddenException("Vacancy belongs to another company");
        }

        VacancyAssignmentDto dto = assignmentDao.findByVacancyId(vacancyId)
                .orElseThrow(() -> new NotFoundException("Assignment not found"));

        assignmentDao.delete(dto);
    }

    private VacancyAssignment toModel(VacancyAssignmentDto dto) {
        VacancyAssignment model = new VacancyAssignment();
        new AssignmentConverter().fromDto(dto, model);
        return model;
    }
}