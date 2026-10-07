package com.maboy.applicantmarket.vacancy.service;

import com.maboy.applicantmarket.auth.service.AuthService;
import com.maboy.applicantmarket.commons.converter.SkillConverter;
import com.maboy.applicantmarket.commons.dao.SkillDao;
import com.maboy.applicantmarket.commons.dao.dto.SkillDto;
import com.maboy.applicantmarket.commons.exception.AlreadyExistsException;
import com.maboy.applicantmarket.commons.exception.NotFoundException;
import com.maboy.applicantmarket.commons.model.Skill;
import com.maboy.applicantmarket.employer.service.CompanyService;
import com.maboy.applicantmarket.vacancy.converter.VacancyRequirementConverter;
import com.maboy.applicantmarket.vacancy.dao.VacancyDao;
import com.maboy.applicantmarket.vacancy.dao.VacancyRequirementDao;
import com.maboy.applicantmarket.vacancy.dao.dto.VacancyDto;
import com.maboy.applicantmarket.vacancy.dao.dto.VacancyRequirementDto;
import com.maboy.applicantmarket.vacancy.model.VacancyRequirement;
import com.maboy.applicantmarket.vacancy.model.enums.VacancyStatus;
import com.maboy.applicantmarket.vacancy.model.request.CreateVacancyRequirementRequest;
import com.maboy.applicantmarket.vacancy.model.request.UpdateVacancyRequirementRequest;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Primary
@Service
public class VacancyRequirementServiceImpl implements VacancyRequirementService {

    private final VacancyRequirementDao requirementDao;
    private final VacancyDao vacancyDao;
    private final SkillDao skillDao;
    private final CompanyService companyService;
    private final AuthService authService;

    public VacancyRequirementServiceImpl(VacancyRequirementDao requirementDao,
                                         VacancyDao vacancyDao,
                                         SkillDao skillDao,
                                         CompanyService companyService,
                                         AuthService authService) {
        this.requirementDao = requirementDao;
        this.vacancyDao = vacancyDao;
        this.skillDao = skillDao;
        this.companyService = companyService;
        this.authService = authService;
    }

    @Override
    @Transactional
    public VacancyRequirement create(UUID ownerId, UUID vacancyId, CreateVacancyRequirementRequest request) {
        authService.requireEmployer(ownerId);
        UUID companyId = companyService.getCompanyIdByOwner(ownerId);

        VacancyDto vacancy = vacancyDao.findByIdAndCompanyId(vacancyId, companyId)
                .orElseThrow(() -> new NotFoundException("Vacancy not found"));

        SkillDto skill = skillDao.findById(request.getSkillId())
                .orElseThrow(() -> new NotFoundException("Skill not found"));

        if (requirementDao.existsByVacancyIdAndSkillId(vacancyId, skill.getId())) {
            throw new AlreadyExistsException("Skill is already added to this vacancy");
        }

        VacancyRequirementDto dto = new VacancyRequirementDto();
        dto.setVacancy(vacancy);
        dto.setSkillId(skill.getId());
        dto.setLevel(request.getLevel());
        dto.setMandatory(request.isMandatory());
        Instant now = Instant.now();
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);

        VacancyRequirementDto saved = requirementDao.save(dto);
        return toModel(saved);
    }

    @Override
    public List<VacancyRequirement> getList(UUID requesterId, UUID vacancyId) {
        VacancyDto vacancy = vacancyDao.findById(vacancyId)
                .orElseThrow(() -> new NotFoundException("Vacancy not found"));

        if (vacancy.getStatus() != VacancyStatus.PUBLISHED) {
            UUID companyId = companyService.getCompanyIdByOwner(requesterId);
            if (!companyId.equals(vacancy.getCompanyId())) {
                throw new NotFoundException("Vacancy not found");
            }
        }

        List<VacancyRequirementDto> found = requirementDao.findAllByVacancyIdOrderByCreatedAtAsc(vacancyId);
        List<VacancyRequirement> result = new ArrayList<>(found.size());
        for (VacancyRequirementDto dto : found) {
            result.add(toModel(dto));
        }
        return result;
    }

    @Override
    @Transactional
    public VacancyRequirement update(UUID ownerId, UUID vacancyId, UUID requirementId, UpdateVacancyRequirementRequest request) {
        authService.requireEmployer(ownerId);
        UUID companyId = companyService.getCompanyIdByOwner(ownerId);

        vacancyDao.findByIdAndCompanyId(vacancyId, companyId)
                .orElseThrow(() -> new NotFoundException("Vacancy not found"));

        VacancyRequirementDto dto = requirementDao.findByIdAndVacancyId(requirementId, vacancyId)
                .orElseThrow(() -> new NotFoundException("Requirement not found"));

        request.getLevel().ifPresent(level -> dto.setLevel(level));
        request.getMandatory().ifPresent(dto::setMandatory);
        dto.setUpdatedAt(Instant.now());

        VacancyRequirementDto saved = requirementDao.save(dto);
        return toModel(saved);
    }

    @Override
    @Transactional
    public void delete(UUID ownerId, UUID vacancyId, UUID requirementId) {
        authService.requireEmployer(ownerId);
        UUID companyId = companyService.getCompanyIdByOwner(ownerId);

        vacancyDao.findByIdAndCompanyId(vacancyId, companyId)
                .orElseThrow(() -> new NotFoundException("Vacancy not found"));

        VacancyRequirementDto dto = requirementDao.findByIdAndVacancyId(requirementId, vacancyId)
                .orElseThrow(() -> new NotFoundException("Requirement not found"));

        requirementDao.delete(dto);
    }

    private VacancyRequirement toModel(VacancyRequirementDto dto) {
        VacancyRequirement model = new VacancyRequirement();
        new VacancyRequirementConverter().fromDto(dto, model);

        skillDao.findById(dto.getSkillId()).ifPresent(skillDto -> {
            Skill skill = new Skill();
            new SkillConverter().fromDto(skillDto, skill);
            model.setSkill(skill);
        });

        return model;
    }
}