package com.maboy.applicantmarket.interaction.service;

import com.maboy.applicantmarket.applicant.api.ApplicantModuleApi;
import com.maboy.applicantmarket.applicant.api.model.ApplicantSummary;
import com.maboy.applicantmarket.auth.service.AuthService;
import com.maboy.applicantmarket.commons.dao.UserDao;
import com.maboy.applicantmarket.commons.dao.dto.UserDto;
import com.maboy.applicantmarket.commons.exception.AccessForbiddenException;
import com.maboy.applicantmarket.commons.exception.AlreadyExistsException;
import com.maboy.applicantmarket.commons.exception.ApplicantNotFoundException;
import com.maboy.applicantmarket.commons.exception.IncorrectRequestDataException;
import com.maboy.applicantmarket.commons.exception.NotFoundException;
import com.maboy.applicantmarket.commons.model.Role;
import com.maboy.applicantmarket.commons.model.UserStatus;
import com.maboy.applicantmarket.employer.service.CompanyService;
import com.maboy.applicantmarket.interaction.converter.InteractionConverter;
import com.maboy.applicantmarket.interaction.dao.InteractionDao;
import com.maboy.applicantmarket.interaction.dao.dto.InteractionDto;
import com.maboy.applicantmarket.interaction.model.Interaction;
import com.maboy.applicantmarket.interaction.model.enums.InteractionStatus;
import com.maboy.applicantmarket.interaction.model.enums.InteractionType;
import com.maboy.applicantmarket.interaction.model.request.CreateApplicationRequest;
import com.maboy.applicantmarket.interaction.model.request.CreateInvitationRequest;
import com.maboy.applicantmarket.interaction.model.response.ContactInfo;
import com.maboy.applicantmarket.vacancy.model.Vacancy;
import com.maboy.applicantmarket.vacancy.model.enums.VacancyStatus;
import com.maboy.applicantmarket.vacancy.service.VacancyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Primary
@Service
public class InteractionServiceImpl implements InteractionService {

    private static final List<InteractionStatus> ACTIVE_STATUSES =
            List.of(InteractionStatus.SENT, InteractionStatus.VIEWED);

    private final InteractionDao interactionDao;
    private final CompanyService companyService;
    private final VacancyService vacancyService;
    private final AuthService authService;
    private final UserDao userDao;
    private final ApplicantModuleApi applicantModuleApi;

    public InteractionServiceImpl(InteractionDao interactionDao,
                                  CompanyService companyService,
                                  VacancyService vacancyService,
                                  AuthService authService,
                                  UserDao userDao,
                                  ApplicantModuleApi applicantModuleApi) {
        this.interactionDao = interactionDao;
        this.companyService = companyService;
        this.vacancyService = vacancyService;
        this.authService = authService;
        this.userDao = userDao;
        this.applicantModuleApi = applicantModuleApi;
    }

    // ---------- Приглашения ----------

    @Override
    @Transactional
    public Interaction createInvitation(UUID ownerId, CreateInvitationRequest request) {
        authService.requireEmployer(ownerId);
        UUID companyId = companyService.getCompanyIdByOwner(ownerId);

        requireActiveCandidate(request.getCandidateId());

        Long salaryFrom = request.getSalaryFrom();
        Long salaryTo = request.getSalaryTo();

        if (request.getVacancyId() != null) {
            Vacancy vacancy = vacancyService.getById(ownerId, request.getVacancyId());
            if (vacancy.getStatus() != VacancyStatus.PUBLISHED) {
                throw new IncorrectRequestDataException("Vacancy is not published");
            }
            if (!vacancy.getCompanyId().equals(companyId)) {
                throw new AccessForbiddenException("Vacancy belongs to another company");
            }
            // Зарплата берётся из вакансии, значения из тела игнорируются.
            salaryFrom = vacancy.getSalaryFrom();
            salaryTo = vacancy.getSalaryTo();
        }

        if (salaryFrom == null || salaryTo == null) {
            throw new IncorrectRequestDataException("Salary must be specified for invitation without vacancy");
        }
        validateSalary(salaryFrom, salaryTo);

        if (request.getVacancyId() != null) {
            boolean exists = interactionDao.existsByEmployerIdAndCandidateIdAndVacancyIdAndTypeAndStatusIn(
                    companyId, request.getCandidateId(), request.getVacancyId(),
                    InteractionType.INVITATION, ACTIVE_STATUSES);
            if (exists) {
                throw new AlreadyExistsException("Active invitation already exists for this vacancy");
            }
        } else {
            boolean exists = interactionDao.existsByEmployerIdAndCandidateIdAndVacancyIdIsNullAndTypeAndStatusIn(
                    companyId, request.getCandidateId(), InteractionType.INVITATION, ACTIVE_STATUSES);
            if (exists) {
                throw new AlreadyExistsException("Active invitation already exists for this candidate");
            }
        }

        InteractionDto dto = new InteractionDto();
        dto.setType(InteractionType.INVITATION);
        dto.setEmployerId(companyId);
        dto.setCandidateId(request.getCandidateId());
        dto.setVacancyId(request.getVacancyId());
        dto.setTitle(request.getTitle());
        dto.setMessage(request.getMessage());
        dto.setSalaryFrom(salaryFrom);
        dto.setSalaryTo(salaryTo);
        dto.setStatus(InteractionStatus.SENT);

        InteractionDto saved = interactionDao.save(dto);
        return toModel(saved);
    }

    @Override
    public List<Interaction> getOutgoingInvitations(UUID ownerId) {
        authService.requireEmployer(ownerId);
        UUID companyId = companyService.getCompanyIdByOwner(ownerId);
        return toModelList(interactionDao.findAllByEmployerIdAndTypeOrderByCreatedAtDesc(
                companyId, InteractionType.INVITATION));
    }

    @Override
    public List<Interaction> getIncomingInvitations(UUID candidateId) {
        authService.requireApplicant(candidateId);
        return toModelList(interactionDao.findAllByCandidateIdAndTypeOrderByCreatedAtDesc(
                candidateId, InteractionType.INVITATION));
    }

    @Override
    @Transactional
    public Interaction acceptInvitation(UUID candidateId, UUID interactionId) {
        InteractionDto dto = requireInteractionForCandidate(interactionId, candidateId, InteractionType.INVITATION);
        requireStatusTransition(dto, InteractionStatus.ACCEPTED);

        Instant now = Instant.now();
        dto.setStatus(InteractionStatus.ACCEPTED);
        dto.setRespondedAt(now);
        dto.setContactsRevealedAt(now);

        return toModel(interactionDao.save(dto));
    }

    @Override
    @Transactional
    public Interaction rejectInvitation(UUID candidateId, UUID interactionId) {
        InteractionDto dto = requireInteractionForCandidate(interactionId, candidateId, InteractionType.INVITATION);
        requireStatusTransition(dto, InteractionStatus.REJECTED);

        dto.setStatus(InteractionStatus.REJECTED);
        dto.setRespondedAt(Instant.now());

        return toModel(interactionDao.save(dto));
    }

    @Override
    @Transactional
    public Interaction withdrawInvitation(UUID ownerId, UUID interactionId) {
        authService.requireEmployer(ownerId);
        UUID companyId = companyService.getCompanyIdByOwner(ownerId);

        InteractionDto dto = interactionDao.findByIdAndEmployerId(interactionId, companyId)
                .orElseThrow(() -> new NotFoundException("Interaction not found"));

        if (dto.getType() != InteractionType.INVITATION) {
            throw new NotFoundException("Interaction not found");
        }

        requireStatusTransition(dto, InteractionStatus.WITHDRAWN);

        dto.setStatus(InteractionStatus.WITHDRAWN);
        dto.setRespondedAt(Instant.now());

        return toModel(interactionDao.save(dto));
    }

    // ---------- Отклики ----------

    @Override
    @Transactional
    public Interaction createApplication(UUID candidateId, UUID vacancyId, CreateApplicationRequest request) {
        authService.requireApplicant(candidateId);

        Vacancy vacancy = vacancyService.getById(candidateId, vacancyId);
        if (vacancy.getStatus() != VacancyStatus.PUBLISHED) {
            throw new IncorrectRequestDataException("Vacancy is not published");
        }

        boolean exists = interactionDao.existsByCandidateIdAndVacancyIdAndTypeAndStatusIn(
                candidateId, vacancyId, InteractionType.APPLICATION, ACTIVE_STATUSES);
        if (exists) {
            throw new AlreadyExistsException("Active application already exists for this vacancy");
        }

        InteractionDto dto = new InteractionDto();
        dto.setType(InteractionType.APPLICATION);
        dto.setEmployerId(vacancy.getCompanyId());
        dto.setCandidateId(candidateId);
        dto.setVacancyId(vacancyId);
        dto.setMessage(request.getMessage());
        dto.setSalaryFrom(vacancy.getSalaryFrom());
        dto.setSalaryTo(vacancy.getSalaryTo());
        dto.setStatus(InteractionStatus.SENT);
        // Отклик инициирован самим кандидатом, значит контакты раскрыты сразу.
        dto.setContactsRevealedAt(Instant.now());

        InteractionDto saved = interactionDao.save(dto);
        return toModel(saved);
    }

    @Override
    public List<Interaction> getMyApplications(UUID candidateId) {
        authService.requireApplicant(candidateId);
        return toModelList(interactionDao.findAllByCandidateIdAndTypeOrderByCreatedAtDesc(
                candidateId, InteractionType.APPLICATION));
    }

    @Override
    public List<Interaction> getIncomingApplications(UUID ownerId) {
        authService.requireEmployer(ownerId);
        UUID companyId = companyService.getCompanyIdByOwner(ownerId);
        return toModelList(interactionDao.findAllByEmployerIdAndTypeOrderByCreatedAtDesc(
                companyId, InteractionType.APPLICATION));
    }

    @Override
    @Transactional
    public Interaction acceptApplication(UUID ownerId, UUID interactionId) {
        authService.requireEmployer(ownerId);
        UUID companyId = companyService.getCompanyIdByOwner(ownerId);

        InteractionDto dto = interactionDao.findByIdAndEmployerId(interactionId, companyId)
                .orElseThrow(() -> new NotFoundException("Interaction not found"));

        if (dto.getType() != InteractionType.APPLICATION) {
            throw new NotFoundException("Interaction not found");
        }

        requireStatusTransition(dto, InteractionStatus.ACCEPTED);

        dto.setStatus(InteractionStatus.ACCEPTED);
        dto.setRespondedAt(Instant.now());

        return toModel(interactionDao.save(dto));
    }

    @Override
    @Transactional
    public Interaction rejectApplication(UUID ownerId, UUID interactionId) {
        authService.requireEmployer(ownerId);
        UUID companyId = companyService.getCompanyIdByOwner(ownerId);

        InteractionDto dto = interactionDao.findByIdAndEmployerId(interactionId, companyId)
                .orElseThrow(() -> new NotFoundException("Interaction not found"));

        if (dto.getType() != InteractionType.APPLICATION) {
            throw new NotFoundException("Interaction not found");
        }

        requireStatusTransition(dto, InteractionStatus.REJECTED);

        dto.setStatus(InteractionStatus.REJECTED);
        dto.setRespondedAt(Instant.now());

        return toModel(interactionDao.save(dto));
    }

    @Override
    @Transactional
    public Interaction withdrawApplication(UUID candidateId, UUID interactionId) {
        InteractionDto dto = requireInteractionForCandidate(interactionId, candidateId, InteractionType.APPLICATION);
        requireStatusTransition(dto, InteractionStatus.WITHDRAWN);

        dto.setStatus(InteractionStatus.WITHDRAWN);
        dto.setRespondedAt(Instant.now());

        return toModel(interactionDao.save(dto));
    }

    // ---------- Общее ----------

    @Override
    @Transactional
    public Interaction getById(UUID requesterId, UUID interactionId) {
        InteractionDto dto = interactionDao.findById(interactionId)
                .orElseThrow(() -> new NotFoundException("Interaction not found"));

        boolean isCandidate = dto.getCandidateId().equals(requesterId);
        boolean isOwner = isOwnerOf(dto.getEmployerId(), requesterId);

        if (!isCandidate && !isOwner) {
            throw new AccessForbiddenException("You do not have access to this interaction");
        }

        // Если это входящее и статус SENT — помечаем как VIEWED.
        if (isCandidate && dto.getStatus() == InteractionStatus.SENT) {
            dto.setStatus(InteractionStatus.VIEWED);
            dto.setViewedAt(Instant.now());
            dto = interactionDao.save(dto);
        }

        return toModel(dto);
    }

    @Override
    public ContactInfo getContacts(UUID ownerId, UUID interactionId) {
        authService.requireEmployer(ownerId);
        UUID companyId = companyService.getCompanyIdByOwner(ownerId);

        InteractionDto dto = interactionDao.findByIdAndEmployerId(interactionId, companyId)
                .orElseThrow(() -> new NotFoundException("Interaction not found"));

        if (dto.getContactsRevealedAt() == null) {
            throw new AccessForbiddenException("Contacts are not revealed yet");
        }

        UserDto user = userDao.findById(dto.getCandidateId())
                .orElseThrow(() -> new NotFoundException("Candidate not found"));

        ContactInfo.ContactInfoBuilder builder = ContactInfo.builder()
                .email(user.getEmail());

        // Имя из applicant. Если профиля нет — оставляем только email.
        try {
            ApplicantSummary summary = applicantModuleApi.getSummary(dto.getCandidateId());
            builder.displayName(summary.getDisplayName());
        } catch (ApplicantNotFoundException ignored) {
            log.debug("Applicant profile not found for user={}", dto.getCandidateId());
        }

        return builder.build();
    }

    // ---------- helpers ----------

    private InteractionDto requireInteractionForCandidate(UUID interactionId, UUID candidateId, InteractionType expectedType) {
        InteractionDto dto = interactionDao.findByIdAndCandidateId(interactionId, candidateId)
                .orElseThrow(() -> new NotFoundException("Interaction not found"));

        if (dto.getType() != expectedType) {
            throw new NotFoundException("Interaction not found");
        }
        return dto;
    }

    private void requireStatusTransition(InteractionDto dto, InteractionStatus target) {
        if (dto.getStatus() != InteractionStatus.SENT && dto.getStatus() != InteractionStatus.VIEWED) {
            throw new IncorrectRequestDataException(
                    "Interaction is already in terminal state: " + dto.getStatus());
        }
    }

    /**
     * Приглашать можно только существующего активного пользователя с ролью APPLICANT.
     * PENDING_EMAIL и BLOCKED отсекаются — им бессмысленно слать приглашение.
     */
    private void requireActiveCandidate(UUID candidateId) {
        UserDto user = userDao.findById(candidateId)
                .orElseThrow(() -> new NotFoundException("Candidate not found"));
        if (user.getRole() != Role.APPLICANT) {
            throw new IncorrectRequestDataException("User is not applicant");
        }
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IncorrectRequestDataException("Candidate is not active");
        }
    }

    private void validateSalary(Long from, Long to) {
        if (from != null && to != null && from > to) {
            throw new IncorrectRequestDataException("Salary from must not exceed salary to");
        }
    }

    private boolean isOwnerOf(UUID companyId, UUID requesterId) {
        try {
            UUID requesterCompanyId = companyService.getCompanyIdByOwner(requesterId);
            return requesterCompanyId.equals(companyId);
        } catch (Exception e) {
            return false;
        }
    }

    private Interaction toModel(InteractionDto dto) {
        Interaction model = new Interaction();
        new InteractionConverter().fromDto(dto, model);
        return model;
    }

    private List<Interaction> toModelList(List<InteractionDto> list) {
        List<Interaction> result = new ArrayList<>(list.size());
        for (InteractionDto dto : list) {
            result.add(toModel(dto));
        }
        return result;
    }
}