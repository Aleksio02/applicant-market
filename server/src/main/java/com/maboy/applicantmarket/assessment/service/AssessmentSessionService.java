package com.maboy.applicantmarket.assessment.service;

import com.maboy.applicantmarket.applicant.api.ApplicantModuleApi;
import com.maboy.applicantmarket.applicant.api.event.AssessmentCompleted;
import com.maboy.applicantmarket.assessment.config.AssessmentProperties;
import com.maboy.applicantmarket.assessment.converter.AssessmentAnswerConverter;
import com.maboy.applicantmarket.assessment.converter.AssessmentItemConverter;
import com.maboy.applicantmarket.assessment.converter.AssessmentSessionConverter;
import com.maboy.applicantmarket.assessment.dao.AssessmentAnswerDao;
import com.maboy.applicantmarket.assessment.dao.AssessmentItemDao;
import com.maboy.applicantmarket.assessment.dao.AssessmentSessionDao;
import com.maboy.applicantmarket.assessment.dao.dto.AssessmentAnswerDto;
import com.maboy.applicantmarket.assessment.dao.dto.AssessmentItemDto;
import com.maboy.applicantmarket.assessment.dao.dto.AssessmentSessionDto;
import com.maboy.applicantmarket.assessment.model.AssessmentAnswer;
import com.maboy.applicantmarket.assessment.model.AssessmentItem;
import com.maboy.applicantmarket.assessment.model.AssessmentSession;
import com.maboy.applicantmarket.assessment.model.exception.*;
import com.maboy.applicantmarket.assessment.service.evaluation.AnswerEvaluator;
import com.maboy.applicantmarket.assessment.service.evaluation.AnswerEvaluatorRegistry;
import com.maboy.applicantmarket.assessment.service.generation.AssessmentSessionGenerator;
import com.maboy.applicantmarket.assessment.service.grading.AssessmentGradingService;
import com.maboy.applicantmarket.assessment.service.grading.GradeResolver;
import com.maboy.applicantmarket.assessment.service.grading.GradingOutcome;
import com.maboy.applicantmarket.commons.exception.assessment.AssessmentAnswerAlreadyExistsException;
import com.maboy.applicantmarket.commons.exception.assessment.AssessmentCooldownException;
import com.maboy.applicantmarket.commons.exception.assessment.AssessmentItemNotFoundException;
import com.maboy.applicantmarket.commons.exception.assessment.AssessmentNotReadyForCompletionException;
import com.maboy.applicantmarket.commons.exception.assessment.AssessmentRetryCooldownException;
import com.maboy.applicantmarket.commons.exception.assessment.AssessmentSessionAlreadyActiveException;
import com.maboy.applicantmarket.commons.exception.assessment.AssessmentSessionExpiredException;
import com.maboy.applicantmarket.commons.exception.assessment.AssessmentSessionNotFoundException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssessmentSessionService {

    private static final List<String> ACTIVE_STATUSES =
        List.of("SURVEY", "IN_PROGRESS");

    private final AssessmentSessionDao sessionDao;
    private final AssessmentItemDao itemDao;
    private final AssessmentAnswerDao answerDao;

    private final AssessmentSessionConverter sessionConverter;
    private final AssessmentItemConverter itemConverter;
    private final AssessmentAnswerConverter answerConverter;

    private final AssessmentSessionGenerator sessionGenerator;
    private final AnswerEvaluatorRegistry evaluatorRegistry;
    private final AssessmentGradingService gradingService;
    private final GradeResolver gradeResolver;

    private final ApplicantModuleApi applicantModuleApi;
    private final AssessmentProperties properties;
    private final ApplicationEventPublisher eventPublisher;

    // ============================================================
    // START SESSION
    // ============================================================
    @Transactional
    public AssessmentSession startSession(UUID userId, UUID skillId, UUID claimedGradeId) {
        UUID applicantId = applicantModuleApi.getApplicantIdByUserId(userId);
        Instant now = Instant.now();

        // 1. Есть ли активная сессия по этому навыку?
        Optional<AssessmentSessionDto> existing = sessionDao
            .findByApplicantIdAndSkillIdAndStatusIn(applicantId, skillId, ACTIVE_STATUSES);
        if (existing.isPresent()) {
            AssessmentSession active = sessionConverter.toModel(existing.get());
            if (active.isExpired(now)) {
                // lazy-expiry: закрываем старую и идём дальше
                active.markExpired(now);
                sessionConverter.applyModelToDto(active, existing.get());
                sessionDao.save(existing.get());
            } else {
                throw new AssessmentSessionAlreadyActiveException(active.getId());
            }
        }

        // 2. Cooldown на смену грейда (applicant_skills.last_grade_change_at)
        if (!applicantModuleApi.canChangeGrade(applicantId, skillId)) {
            throw new AssessmentCooldownException(skillId);
        }

        // 3. Retry cooldown: не даём сразу пересдавать тот же грейд или выше
        Optional<AssessmentSessionDto> lastTerminal = sessionDao
            .findTopByApplicantIdAndSkillIdAndStatusInOrderByCompletedAtDesc(
                applicantId, skillId, List.of("COMPLETED", "FAILED"));
        if (lastTerminal.isPresent()) {
            AssessmentSessionDto last = lastTerminal.get();
            Instant completedAt = last.getCompletedAt();
            if (completedAt != null) {
                Duration elapsed = Duration.between(completedAt, now);
                Duration retryCooldown = properties.getSession().getRetryCooldown();
                if (elapsed.compareTo(retryCooldown) < 0) {
                    int newLevel = gradeResolver.getLevel(claimedGradeId);
                    int lastLevel = gradeResolver.getLevel(last.getClaimedGradeId());
                    if (newLevel >= lastLevel) {
                        log.info("Retry cooldown blocked: applicant={}, skill={}, " +
                                 "lastAttempt={}, elapsed={}, cooldown={}",
                            applicantId, skillId, completedAt, elapsed, retryCooldown);
                        throw new AssessmentRetryCooldownException(skillId, completedAt);
                    }
                    log.info("Retry allowed on lower grade: applicant={}, skill={}, " +
                             "last={}, new={}",
                        applicantId, skillId, last.getClaimedGradeId(), claimedGradeId);
                }
            }
        }

        // 4. Создать и сохранить сессию
        Instant expiresAt = now.plus(properties.getSession().getExpiresIn());

        AssessmentSession session = AssessmentSession.builder()
            .applicantId(applicantId)
            .skillId(skillId)
            .claimedGradeId(claimedGradeId)
            .status("IN_PROGRESS")
            .expiresAt(expiresAt)
            .build();

        AssessmentSessionDto sessionEntity = sessionConverter.toNewDto(session);
        try {
            sessionEntity = sessionDao.save(sessionEntity);
        } catch (DataIntegrityViolationException e) {
            // Параллельный запрос успел создать активную сессию раньше.
            // Партиальный unique index uq_assessment_sessions_active не дал вставить вторую.
            UUID winnerId = sessionDao
                .findByApplicantIdAndSkillIdAndStatusIn(applicantId, skillId, ACTIVE_STATUSES)
                .map(AssessmentSessionDto::getId)
                .orElse(null);
            log.warn("Concurrent active session creation for applicant={}, skill={}",
                applicantId, skillId);
            throw new AssessmentSessionAlreadyActiveException(winnerId);
        }
        AssessmentSession persisted = sessionConverter.toModel(sessionEntity);

        // 5. Сгенерировать items
        List<AssessmentItem> items = sessionGenerator.generateFor(skillId, claimedGradeId);

        // 6. Проставить sessionId и сохранить
        List<AssessmentItemDto> itemEntities = new ArrayList<>(items.size());
        for (AssessmentItem item : items) {
            item.setSessionId(persisted.getId());
            itemEntities.add(itemConverter.toNewDto(item));
        }
        itemDao.saveAll(itemEntities);

        log.info("Assessment session started: id={}, applicant={}, skill={}, claimed={}, items={}",
            persisted.getId(), applicantId, skillId, claimedGradeId, items.size());

        return persisted;
    }

    // ============================================================
    // GET SESSION
    // ============================================================
    @Transactional
    public AssessmentSession getSession(UUID userId, UUID sessionId) {
        UUID applicantId = applicantModuleApi.getApplicantIdByUserId(userId);
        AssessmentSession session = loadOwnedSession(sessionId, applicantId);
        applyLazyExpiry(session);
        return session;
    }

    // ============================================================
    // SUBMIT ANSWER
    // ============================================================
    @Transactional
    public AnswerResult submitAnswer(UUID userId, UUID sessionId, UUID itemId, Map<String, Object> answerJson) {
        UUID applicantId = applicantModuleApi.getApplicantIdByUserId(userId);
        AssessmentSession session = loadOwnedSession(sessionId, applicantId);

        Instant now = Instant.now();
        if (session.isExpired(now)) {
            session.markExpired(now);
            persist(session);
            throw new AssessmentSessionExpiredException("Assessment session expired for id %s".formatted(sessionId.toString()));
        }
        session.ensureActive(now);

        // item должен принадлежать этой сессии
        AssessmentItemDto itemEntity = itemDao.findById(itemId)
            .orElseThrow(() -> AssessmentItemNotFoundException.byId(itemId));
        if (!itemEntity.getSessionId().equals(sessionId)) {
            throw AssessmentItemNotFoundException.byId(itemId);
        }
        AssessmentItem item = itemConverter.toModel(itemEntity);

        // запрет на повторный ответ
        if (answerDao.findByItemId(itemId).isPresent()) {
            throw new AssessmentAnswerAlreadyExistsException(itemId);
        }

        // оценка
        AnswerEvaluator evaluator = evaluatorRegistry.get(item.getType());
        boolean correct;
        try {
            correct = evaluator.evaluate(item.getCorrectAnswer(), answerJson);
        } catch (RuntimeException e) {
            throw new AnswerEvaluationException(
                "Failed to evaluate answer for item " + itemId, e);
        }
        short score = (short) (correct ? item.getPoints() : 0);

        AssessmentAnswer answer = AssessmentAnswer.builder()
            .itemId(itemId)
            .answerJson(answerJson)
            .correct(correct)
            .score(score)
            .build();
        answerDao.save(answerConverter.toNewDto(answer));

        return new AnswerResult(itemId, correct, score);
    }

    // ============================================================
    // COMPLETE SESSION
    // ============================================================
    @Transactional
    public GradingOutcome completeSession(UUID userId, UUID sessionId) {
        UUID applicantId = applicantModuleApi.getApplicantIdByUserId(userId);
        AssessmentSession session = loadOwnedSession(sessionId, applicantId);

        Instant now = Instant.now();
        if (session.isExpired(now)) {
            session.markExpired(now);
            persist(session);
            throw new AssessmentSessionExpiredException("Assessment session expired for id %s".formatted(sessionId.toString()));
        }
        session.ensureActive(now);

        // 1. Загрузить items и answers
        List<AssessmentItemDto> itemEntities = itemDao.findAllBySessionIdOrderByPositionAsc(sessionId);
        if (itemEntities.isEmpty()) {
            throw new AssessmentNotReadyForCompletionException(0, 0);
        }
        List<AssessmentItem> items = itemEntities.stream()
            .map(itemConverter::toModel)
            .toList();

        List<UUID> itemIds = items.stream().map(AssessmentItem::getId).toList();
        List<AssessmentAnswerDto> answerEntities = answerDao.findAllByItemIdIn(itemIds);
        List<AssessmentAnswer> answers = answerEntities.stream()
            .map(answerConverter::toModel)
            .toList();

        if (answers.size() != items.size()) {
            throw new AssessmentNotReadyForCompletionException(items.size(), answers.size());
        }

        // 2. Оценка
        GradingOutcome outcome = gradingService.grade(
            session.getClaimedGradeId(), items, answers);

        // 3. Обновить сессию
        if (outcome.isCompleted()) {
            session.complete(outcome.resultGradeId(), outcome.score(), now);
        } else {
            session.fail(outcome.score(), now);
        }
        persist(session);

        // 4. Событие — только если грейд присвоен
        if (outcome.isCompleted()) {
            eventPublisher.publishEvent(new AssessmentCompleted(
                applicantId,
                session.getSkillId(),
                outcome.resultGradeId(),
                outcome.score().doubleValue()
            ));
        }

        log.info("Assessment session completed: id={}, status={}, score={}, resultGrade={}",
            sessionId, outcome.status(), outcome.score(), outcome.resultGradeId()
        );

        return outcome;
    }

    // ============================================================
    // GET HISTORY
    // ============================================================
    @Transactional(readOnly = true)
    public List<AssessmentSession> getHistory(UUID userId) {
        UUID applicantId = applicantModuleApi.getApplicantIdByUserId(userId);
        return sessionDao.findAllByApplicantIdOrderByStartedAtDesc(applicantId).stream()
            .map(sessionConverter::toModel)
            .toList();
    }

    // ============================================================
    // LOAD ITEMS
    // ============================================================
    @Transactional(readOnly = true)
    public List<AssessmentItem> loadItems(UUID sessionId) {
        return itemDao.findAllBySessionIdOrderByPositionAsc(sessionId).stream()
            .map(itemConverter::toModel)
            .toList();
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private AssessmentSession loadOwnedSession(UUID sessionId, UUID applicantId) {
        AssessmentSessionDto entity = sessionDao.findByIdAndApplicantId(sessionId, applicantId)
            .orElseThrow(() -> AssessmentSessionNotFoundException.byId(sessionId));
        return sessionConverter.toModel(entity);
    }

    private void applyLazyExpiry(AssessmentSession session) {
        Instant now = Instant.now();
        if (session.isExpired(now)) {
            session.markExpired(now);
            persist(session);
            throw new AssessmentSessionExpiredException("Assessment session expired for id %s".formatted(session.getId().toString()));
        }
    }

    private void persist(AssessmentSession session) {
        AssessmentSessionDto entity = sessionDao.findById(session.getId())
            .orElseThrow(() -> AssessmentSessionNotFoundException.byId(session.getId()));
        sessionConverter.applyModelToDto(session, entity);
        sessionDao.save(entity);
    }

    // ============================================================
    // Value object для ответа submitAnswer
    // ============================================================
    public record AnswerResult(UUID itemId, boolean correct, Short score) {}
}