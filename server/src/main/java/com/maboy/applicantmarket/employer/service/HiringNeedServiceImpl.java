package com.maboy.applicantmarket.employer.service;

import com.maboy.applicantmarket.auth.service.AuthService;
import com.maboy.applicantmarket.commons.converter.GradeConverter;
import com.maboy.applicantmarket.commons.converter.SpecializationConverter;
import com.maboy.applicantmarket.commons.dao.GradeDao;
import com.maboy.applicantmarket.commons.dao.SpecializationDao;
import com.maboy.applicantmarket.commons.dao.dto.GradeDto;
import com.maboy.applicantmarket.commons.dao.dto.SpecializationDto;
import com.maboy.applicantmarket.commons.exception.IncorrectRequestDataException;
import com.maboy.applicantmarket.commons.exception.NotFoundException;
import com.maboy.applicantmarket.commons.model.Grade;
import com.maboy.applicantmarket.commons.model.Specialization;
import com.maboy.applicantmarket.commons.model.response.PageResponse;
import com.maboy.applicantmarket.employer.converter.HiringNeedConverter;
import com.maboy.applicantmarket.employer.dao.CompanyDao;
import com.maboy.applicantmarket.employer.dao.HiringNeedDao;
import com.maboy.applicantmarket.employer.dao.dto.CompanyDto;
import com.maboy.applicantmarket.employer.dao.dto.HiringNeedDto;
import com.maboy.applicantmarket.employer.model.HiringNeed;
import com.maboy.applicantmarket.employer.model.request.CreateHiringNeedRequest;
import com.maboy.applicantmarket.employer.model.request.GetHiringNeedListRequest;
import com.maboy.applicantmarket.employer.model.request.UpdateHiringNeedRequest;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Primary
@Service
public class HiringNeedServiceImpl implements HiringNeedService {

    private final HiringNeedDao hiringNeedDao;
    private final CompanyDao companyDao;
    private final SpecializationDao specializationDao;
    private final GradeDao gradeDao;
    private final AuthService authService;

    public HiringNeedServiceImpl(HiringNeedDao hiringNeedDao,
                                 CompanyDao companyDao,
                                 SpecializationDao specializationDao,
                                 GradeDao gradeDao,
                                 AuthService authService) {
        this.hiringNeedDao = hiringNeedDao;
        this.companyDao = companyDao;
        this.specializationDao = specializationDao;
        this.gradeDao = gradeDao;
        this.authService = authService;
    }

    @Override
    @Transactional
    public HiringNeed create(UUID ownerId, CreateHiringNeedRequest request) {
        authService.requireEmployer(ownerId);
        CompanyDto company = requireCompany(ownerId);
        validateSalary(request.getSalaryFrom(), request.getSalaryTo());

        SpecializationDto specialization = specializationDao.findById(request.getSpecializationId())
                .orElseThrow(() -> new NotFoundException("Specialization not found"));
        GradeDto grade = gradeDao.findById(request.getGradeId())
                .orElseThrow(() -> new NotFoundException("Grade not found"));

        HiringNeedDto dto = new HiringNeedDto();
        dto.setCompany(company);
        dto.setTitle(request.getTitle());
        dto.setDescription(request.getDescription());
        dto.setSpecializationId(specialization.getId());
        dto.setGradeId(grade.getId());
        dto.setSalaryFrom(request.getSalaryFrom());
        dto.setSalaryTo(request.getSalaryTo());
        dto.setFormat(request.getFormat());
        dto.setLocation(request.getLocation());
        dto.setActive(true);
        Instant now = Instant.now();
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);

        HiringNeedDto saved = hiringNeedDao.save(dto);
        return toModel(saved);
    }

    @Override
    public PageResponse<HiringNeed> getList(UUID ownerId, GetHiringNeedListRequest request) {
        authService.requireEmployer(ownerId);
        CompanyDto company = requireCompany(ownerId);

        int page = request.getPage() != null ? request.getPage() : 0;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 20;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<HiringNeedDto> found = request.getActive() != null
                ? hiringNeedDao.findAllByCompanyIdAndActive(company.getId(), request.getActive(), pageable)
                : hiringNeedDao.findAllByCompanyId(company.getId(), pageable);

        List<HiringNeed> content = new ArrayList<>(found.getNumberOfElements());
        for (HiringNeedDto dto : found) {
            content.add(toModel(dto));
        }

        return PageResponse.<HiringNeed>builder()
                .content(content)
                .totalElements(found.getTotalElements())
                .totalPages(found.getTotalPages())
                .page(found.getNumber())
                .pageSize(found.getSize())
                .build();
    }

    @Override
    public HiringNeed getById(UUID ownerId, UUID id) {
        authService.requireEmployer(ownerId);
        CompanyDto company = requireCompany(ownerId);

        HiringNeedDto dto = hiringNeedDao.findByIdAndCompanyId(id, company.getId())
                .orElseThrow(() -> new NotFoundException("Hiring need not found"));

        return toModel(dto);
    }

    @Override
    @Transactional
    public HiringNeed update(UUID ownerId, UUID id, UpdateHiringNeedRequest request) {
        authService.requireEmployer(ownerId);
        CompanyDto company = requireCompany(ownerId);

        HiringNeedDto dto = hiringNeedDao.findByIdAndCompanyId(id, company.getId())
                .orElseThrow(() -> new NotFoundException("Hiring need not found"));

        request.getTitle().ifPresent(dto::setTitle);
        request.getDescription().ifPresent(dto::setDescription);

        request.getSpecializationId().ifPresent(specId -> {
            SpecializationDto spec = specializationDao.findById(specId)
                    .orElseThrow(() -> new NotFoundException("Specialization not found"));
            dto.setSpecializationId(spec.getId());
        });

        request.getGradeId().ifPresent(gradeId -> {
            GradeDto grade = gradeDao.findById(gradeId)
                    .orElseThrow(() -> new NotFoundException("Grade not found"));
            dto.setGradeId(grade.getId());
        });

        request.getSalaryFrom().ifPresent(dto::setSalaryFrom);
        request.getSalaryTo().ifPresent(dto::setSalaryTo);
        request.getFormat().ifPresent(dto::setFormat);
        request.getLocation().ifPresent(dto::setLocation);

        validateSalary(dto.getSalaryFrom(), dto.getSalaryTo());
        dto.setUpdatedAt(Instant.now());

        HiringNeedDto saved = hiringNeedDao.save(dto);
        return toModel(saved);
    }

    @Override
    @Transactional
    public HiringNeed activate(UUID ownerId, UUID id) {
        authService.requireEmployer(ownerId);
        CompanyDto company = requireCompany(ownerId);

        HiringNeedDto dto = hiringNeedDao.findByIdAndCompanyId(id, company.getId())
                .orElseThrow(() -> new NotFoundException("Hiring need not found"));

        dto.setActive(true);
        dto.setUpdatedAt(Instant.now());

        HiringNeedDto saved = hiringNeedDao.save(dto);
        return toModel(saved);
    }

    @Override
    @Transactional
    public HiringNeed deactivate(UUID ownerId, UUID id) {
        authService.requireEmployer(ownerId);
        CompanyDto company = requireCompany(ownerId);

        HiringNeedDto dto = hiringNeedDao.findByIdAndCompanyId(id, company.getId())
                .orElseThrow(() -> new NotFoundException("Hiring need not found"));

        dto.setActive(false);
        dto.setUpdatedAt(Instant.now());

        HiringNeedDto saved = hiringNeedDao.save(dto);
        return toModel(saved);
    }

    private CompanyDto requireCompany(UUID ownerId) {
        return companyDao.findByOwnerId(ownerId)
                .orElseThrow(() -> new NotFoundException("Company not found"));
    }

    private void validateSalary(Long from, Long to) {
        if (from != null && to != null && from > to) {
            throw new IncorrectRequestDataException("Salary from must not exceed salary to");
        }
    }

    private HiringNeed toModel(HiringNeedDto dto) {
        HiringNeed model = new HiringNeed();
        new HiringNeedConverter().fromDto(dto, model);

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