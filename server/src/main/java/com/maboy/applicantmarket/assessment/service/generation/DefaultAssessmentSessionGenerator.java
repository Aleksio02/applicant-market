package com.maboy.applicantmarket.assessment.service.generation;

import com.maboy.applicantmarket.assessment.config.AssessmentProperties;
import com.maboy.applicantmarket.assessment.dao.AssessmentTemplateDao;
import com.maboy.applicantmarket.assessment.dao.AssessmentTemplateStatsDao;
import com.maboy.applicantmarket.assessment.dao.dto.AssessmentTemplateDto;
import com.maboy.applicantmarket.assessment.dao.dto.AssessmentTemplateStatsDto;
import com.maboy.applicantmarket.assessment.model.AssessmentItem;
import com.maboy.applicantmarket.assessment.model.AssessmentTemplate;
import com.maboy.applicantmarket.assessment.model.exception.AssessmentItemGenerationException;
import com.maboy.applicantmarket.commons.exception.assessment.AssessmentTemplatePoolExhaustedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultAssessmentSessionGenerator implements AssessmentSessionGenerator {

    private static final short MIN_DIFFICULTY = 1;
    private static final short MAX_DIFFICULTY = 5;

    private final AssessmentTemplateDao templateDao;
    private final AssessmentTemplateStatsDao statsDao;
    private final AssessmentItemFactory itemFactory;
    private final GradeDifficultyResolver gradeDifficultyResolver;
    private final AssessmentProperties properties;

    @Override
    public List<AssessmentItem> generateFor(UUID skillId, UUID claimedGradeId) {
        short claimedDifficulty = gradeDifficultyResolver.resolveDifficulty(claimedGradeId);
        long sessionSeed = ThreadLocalRandom.current().nextLong();
        Random masterRnd = new Random(sessionSeed);

        List<Short> plan = buildDifficultyPlan(skillId, claimedDifficulty, masterRnd);
        List<AssessmentItem> items = new ArrayList<>(plan.size());
        Set<UUID> usedTemplateIds = new HashSet<>();

        for (int i = 0; i < plan.size(); i++) {
            int position = i + 1;
            short difficulty = plan.get(i);
            long itemSeed = masterRnd.nextLong();

            AssessmentItem item = pickAndGenerate(
                    skillId, difficulty, position, itemSeed, usedTemplateIds);
            items.add(item);
            usedTemplateIds.add(item.getTemplateId());
        }

        log.info("Generated {} assessment items for skill={} claimedGrade={} (seed={})",
                items.size(), skillId, claimedGradeId, sessionSeed);
        return items;
    }

    // ============================================================
    // План распределения
    // ============================================================
    private List<Short> buildDifficultyPlan(UUID skillId, short claimedDifficulty, Random masterRnd) {
        var dist = properties.getGeneration().getDistribution();

        short belowDiff = clamp((short) (claimedDifficulty - 1));
        short aboveDiff = clamp((short) (claimedDifficulty + 1));

        List<Short> plan = new ArrayList<>(properties.getGeneration().getTotalItems());
        for (int i = 0; i < dist.getBelowClaimed(); i++) plan.add(belowDiff);
        for (int i = 0; i < dist.getClaimed(); i++)      plan.add(claimedDifficulty);
        for (int i = 0; i < dist.getAboveClaimed(); i++) plan.add(aboveDiff);

        // "Random" — случайный difficulty из доступных для этого навыка.
        for (int i = 0; i < dist.getRandom(); i++) {
            plan.add(pickRandomAvailableDifficulty(skillId, claimedDifficulty, masterRnd));
        }
        return plan;
    }

    private short clamp(short d) {
        if (d < MIN_DIFFICULTY) return MIN_DIFFICULTY;
        if (d > MAX_DIFFICULTY) return MAX_DIFFICULTY;
        return d;
    }

    /**
     * Для "random" берём случайный difficulty из тех, для которых есть
     * хотя бы один активный шаблон. Fallback — claimed.
     */
    private short pickRandomAvailableDifficulty(UUID skillId, short claimedDifficulty, Random masterRnd) {
        List<Short> available = templateDao.findDistinctDifficultiesForSkill(skillId);
        if (available.isEmpty()) {
            return claimedDifficulty;
        }
        return available.get(masterRnd.nextInt(available.size()));
    }

    // ============================================================
    // Подбор шаблона и генерация
    // ============================================================
    private AssessmentItem pickAndGenerate(UUID skillId, short difficulty, int position,
                                           long itemSeed, Set<UUID> usedTemplateIds) {
        List<AssessmentTemplateDto> pool = templateDao
                .findAllBySkillIdAndDifficultyAndIsActiveTrue(skillId, difficulty);

        List<AssessmentTemplate> candidates = new ArrayList<>();
        for (AssessmentTemplateDto e : pool) {
            if (!usedTemplateIds.contains(e.getId())) {
                candidates.add(toModel(e));
            }
        }

        if (candidates.isEmpty()) {
            throw new AssessmentTemplatePoolExhaustedException(
                    skillId, difficulty, 1, 0);
        }

        // Детерминированная перестановка: одни и те же candidates → один порядок.
        Collections.shuffle(candidates, new Random(itemSeed));

        for (AssessmentTemplate template : candidates) {
            try {
                return itemFactory.generate(template, position, itemSeed);
            } catch (AssessmentItemGenerationException e) {
                recordFailure(template, e);
            }
        }

        // Сюда попадаем, только если все кандидаты провалились.
        throw new AssessmentTemplatePoolExhaustedException(
                skillId, difficulty, 1, candidates.size());
    }

    // ============================================================
    // Статистика и автодеактивация
    // ============================================================
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailure(AssessmentTemplate template, AssessmentItemGenerationException e) {
        log.warn("Template generation failed: code={}, reason={}",
            template.getCode(), e.getMessage());
        AssessmentTemplateStatsDto stats = statsDao.findById(template.getId())
                .orElseGet(() -> {
                    AssessmentTemplateStatsDto s = new AssessmentTemplateStatsDto();
                    s.setTemplateId(template.getId());
                    s.setServedCount(0);
                    s.setCorrectCount(0);
                    return s;
                });

        Integer failures = stats.getGenerationFailureCount();
        failures = (failures == null ? 0 : failures) + 1;
        stats.setGenerationFailureCount(failures);
        stats.setLastGenerationFailureAt(Instant.now());
        statsDao.save(stats);

        int threshold = properties.getGeneration().getAutoDeactivateThreshold();
        if (failures >= threshold) {
            deactivateTemplate(template, failures);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deactivateTemplate(AssessmentTemplate template, int failures) {
        AssessmentTemplateDto entity = templateDao.findById(template.getId())
                .orElse(null);
        if (entity == null || Boolean.FALSE.equals(entity.getIsActive())) {
            return;
        }
        entity.setIsActive(false);
        templateDao.save(entity);
        log.error("Auto-deactivated broken template: code={}, totalFailures={}, " +
                  "needs manual review", template.getCode(), failures);
    }

    // ============================================================
    // Конвертеры-хелперы
    // ============================================================
    private AssessmentTemplate toModel(AssessmentTemplateDto e) {
        return AssessmentTemplate.builder()
                .id(e.getId())
                .code(e.getCode())
                .skillId(e.getSkillId())
                .topic(e.getTopic())
                .type(e.getType())
                .difficulty(e.getDifficulty())
                .bodyTemplate(e.getBodyTemplate())
                .parameterSpec(e.getParameterSpec())
                .answerSpec(e.getAnswerSpec())
                .explanationTemplate(e.getExplanationTemplate())
                .active(Boolean.TRUE.equals(e.getIsActive()))
                .build();
    }
}