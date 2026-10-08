package com.maboy.applicantmarket.vacancy.service;

import com.maboy.applicantmarket.auth.service.AuthService;
import com.maboy.applicantmarket.commons.converter.GradeConverter;
import com.maboy.applicantmarket.commons.converter.SpecializationConverter;
import com.maboy.applicantmarket.commons.dao.GradeDao;
import com.maboy.applicantmarket.commons.dao.SpecializationDao;
import com.maboy.applicantmarket.commons.dao.dto.GradeDto;
import com.maboy.applicantmarket.commons.dao.dto.SpecializationDto;
import com.maboy.applicantmarket.commons.exception.AccessForbiddenException;
import com.maboy.applicantmarket.commons.exception.IncorrectRequestDataException;
import com.maboy.applicantmarket.commons.exception.NotFoundException;
import com.maboy.applicantmarket.commons.model.Grade;
import com.maboy.applicantmarket.commons.model.Specialization;
import com.maboy.applicantmarket.commons.model.response.PageResponse;
import com.maboy.applicantmarket.employer.service.CompanyService;
import com.maboy.applicantmarket.vacancy.converter.VacancyConverter;
import com.maboy.applicantmarket.vacancy.dao.VacancyDao;
import com.maboy.applicantmarket.vacancy.dao.dto.VacancyDto;
import com.maboy.applicantmarket.vacancy.model.Vacancy;
import com.maboy.applicantmarket.vacancy.model.enums.VacancyStatus;
import com.maboy.applicantmarket.vacancy.model.request.CreateVacancyRequest;
import com.maboy.applicantmarket.vacancy.model.request.GetVacancyListRequest;
import com.maboy.applicantmarket.vacancy.model.request.UpdateVacancyRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Primary
@Service
public class VacancyServiceImpl implements VacancyService {

    private final VacancyDao vacancyDao;
    private final SpecializationDao specializationDao;
    private final GradeDao gradeDao;
    private final CompanyService companyService;
    private final AuthService authService;

    public VacancyServiceImpl(VacancyDao vacancyDao,
                              SpecializationDao specializationDao,
                              GradeDao gradeDao,
                              CompanyService companyService,
                              AuthService authService) {
        this.vacancyDao = vacancyDao;
        this.specializationDao = specializationDao;
        this.gradeDao = gradeDao;
        this.companyService = companyService;
        this.authService = authService;
    }

    @Override
    @Transactional
    public Vacancy create(UUID ownerId, CreateVacancyRequest request) {
        authService.requireEmployer(ownerId);
        UUID companyId = companyService.getCompanyIdByOwner(ownerId);

        validateSalary(request.getSalaryFrom(), request.getSalaryTo());
        SpecializationDto specialization = requireSpecialization(request.getSpecializationId());
        GradeDto grade = requireGrade(request.getGradeId());

        VacancyDto dto = new VacancyDto();
        dto.setCompanyId(companyId);
        dto.setTitle(request.getTitle());
        dto.setDescription(request.getDescription());
        dto.setSpecializationId(specialization.getId());
        dto.setGradeId(grade.getId());
        dto.setSalaryFrom(request.getSalaryFrom());
        dto.setSalaryTo(request.getSalaryTo());
        dto.setFormat(request.getFormat());
        dto.setLocation(request.getLocation());
        dto.setStatus(VacancyStatus.DRAFT);
        Instant now = Instant.now();
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);

        VacancyDto saved = vacancyDao.save(dto);
        return toModel(saved);
    }

    @Override
    public PageResponse<Vacancy> getListForApplicant(GetVacancyListRequest request) {
        Pageable pageable = buildPageable(request);
        Specification<VacancyDto> spec = buildSpecification(request, null, VacancyStatus.PUBLISHED);
        Page<VacancyDto> found = vacancyDao.findAll(spec, pageable);
        return toPageResponse(found);
    }

    @Override
    public PageResponse<Vacancy> getListForEmployer(UUID ownerId, GetVacancyListRequest request) {
        authService.requireEmployer(ownerId);
        UUID companyId = companyService.getCompanyIdByOwner(ownerId);
        Pageable pageable = buildPageable(request);

        Specification<VacancyDto> spec = buildSpecification(request, companyId, request.getStatus());
        Page<VacancyDto> found = vacancyDao.findAll(spec, pageable);
        return toPageResponse(found);
    }

    @Override
    public Vacancy getById(UUID requesterId, UUID id) {
        VacancyDto dto = vacancyDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Vacancy not found"));

        if (dto.getStatus() != VacancyStatus.PUBLISHED) {
            UUID companyId;
            try {
                companyId = companyService.getCompanyIdByOwner(requesterId);
            } catch (NotFoundException e) {
                throw new AccessForbiddenException("Vacancy is not published");
            }
            if (!companyId.equals(dto.getCompanyId())) {
                throw new AccessForbiddenException("Vacancy is not published");
            }
        }

        return toModel(dto);
    }

    @Override
    @Transactional
    public Vacancy update(UUID ownerId, UUID id, UpdateVacancyRequest request) {
        authService.requireEmployer(ownerId);
        UUID companyId = companyService.getCompanyIdByOwner(ownerId);

        VacancyDto dto = vacancyDao.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new NotFoundException("Vacancy not found"));

        request.getTitle().ifPresent(dto::setTitle);
        request.getDescription().ifPresent(dto::setDescription);

        request.getSpecializationId().ifPresent(specId -> {
            SpecializationDto spec = requireSpecialization(specId);
            dto.setSpecializationId(spec.getId());
        });

        request.getGradeId().ifPresent(gradeId -> {
            GradeDto grade = requireGrade(gradeId);
            dto.setGradeId(grade.getId());
        });

        request.getSalaryFrom().ifPresent(dto::setSalaryFrom);
        request.getSalaryTo().ifPresent(dto::setSalaryTo);
        request.getFormat().ifPresent(dto::setFormat);
        request.getLocation().ifPresent(dto::setLocation);

        validateSalary(dto.getSalaryFrom(), dto.getSalaryTo());
        dto.setUpdatedAt(Instant.now());

        VacancyDto saved = vacancyDao.save(dto);
        return toModel(saved);
    }

    @Override
    @Transactional
    public Vacancy publish(UUID ownerId, UUID id) {
        authService.requireEmployer(ownerId);
        UUID companyId = companyService.getCompanyIdByOwner(ownerId);

        VacancyDto dto = vacancyDao.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new NotFoundException("Vacancy not found"));

        if (dto.getStatus() == VacancyStatus.PUBLISHED) {
            throw new IncorrectRequestDataException("Vacancy is already published");
        }
        if (dto.getStatus() == VacancyStatus.CLOSED) {
            throw new IncorrectRequestDataException("Closed vacancy cannot be published");
        }

        dto.setStatus(VacancyStatus.PUBLISHED);
        dto.setPublishedAt(Instant.now());
        dto.setUpdatedAt(Instant.now());

        VacancyDto saved = vacancyDao.save(dto);
        return toModel(saved);
    }

    @Override
    @Transactional
    public Vacancy close(UUID ownerId, UUID id) {
        authService.requireEmployer(ownerId);
        UUID companyId = companyService.getCompanyIdByOwner(ownerId);

        VacancyDto dto = vacancyDao.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new NotFoundException("Vacancy not found"));

        if (dto.getStatus() == VacancyStatus.CLOSED) {
            throw new IncorrectRequestDataException("Vacancy is already closed");
        }

        dto.setStatus(VacancyStatus.CLOSED);
        dto.setClosedAt(Instant.now());
        dto.setUpdatedAt(Instant.now());

        VacancyDto saved = vacancyDao.save(dto);
        return toModel(saved);
    }

    private void validateSalary(Long from, Long to) {
        if (from != null && to != null && from > to) {
            throw new IncorrectRequestDataException("Salary from must not exceed salary to");
        }
    }

    private SpecializationDto requireSpecialization(UUID id) {
        return specializationDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Specialization not found"));
    }

    private GradeDto requireGrade(UUID id) {
        return gradeDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Grade not found"));
    }

    private Pageable buildPageable(GetVacancyListRequest request) {
        int page = request.getPage() != null ? request.getPage() : 0;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 20;
        return PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private Specification<VacancyDto> buildSpecification(GetVacancyListRequest request, UUID companyId, VacancyStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (companyId != null) {
                predicates.add(cb.equal(root.get("companyId"), companyId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (request.getSpecializationId() != null) {
                predicates.add(cb.equal(root.get("specializationId"), request.getSpecializationId()));
            }
            if (request.getGradeId() != null) {
                predicates.add(cb.equal(root.get("gradeId"), request.getGradeId()));
            }
            if (request.getFormat() != null) {
                predicates.add(cb.equal(root.get("format"), request.getFormat()));
            }
            if (request.getSalaryFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("salaryTo"), request.getSalaryFrom()));
            }
            if (request.getLocation() != null && !request.getLocation().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("location")),
                        "%" + request.getLocation().toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private PageResponse<Vacancy> toPageResponse(Page<VacancyDto> page) {
        List<Vacancy> content = new ArrayList<>(page.getNumberOfElements());
        for (VacancyDto dto : page) {
            content.add(toModel(dto));
        }
        return PageResponse.<Vacancy>builder()
                .content(content)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .page(page.getNumber())
                .pageSize(page.getSize())
                .build();
    }

    private Vacancy toModel(VacancyDto dto) {
        Vacancy model = new Vacancy();
        new VacancyConverter().fromDto(dto, model);

        specializationDao.findById(dto.getSpecializationId()).ifPresent(specDto -> {
            Specialization spec = new Specialization();
            new SpecializationConverter().fromDto(specDto, spec);
            model.setSpecialization(spec);
        });

        gradeDao.findById(dto.getGradeId()).ifPresent(gradeDto -> {
            Grade grade = new Grade();
            new GradeConverter().fromDto(gradeDto, grade);
            model.setGrade(grade);
        });

        return model;
    }
}