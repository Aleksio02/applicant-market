package com.maboy.applicantmarket.matching.service;

import com.maboy.applicantmarket.applicant.api.ApplicantModuleApi;
import com.maboy.applicantmarket.applicant.api.model.ApplicantSummary;
import com.maboy.applicantmarket.commons.dao.dto.CategoryDto;
import com.maboy.applicantmarket.commons.exception.matching.InvalidSearchRequestException;
import com.maboy.applicantmarket.commons.exception.matching.MatchingException;
import com.maboy.applicantmarket.commons.exception.matching.MatchingQueryNotFoundException;
import com.maboy.applicantmarket.matching.config.MatchingProperties;
import com.maboy.applicantmarket.matching.converter.CandidateSearchConverter;
import com.maboy.applicantmarket.matching.dao.MatchingExplanationDao;
import com.maboy.applicantmarket.matching.dao.MatchingQueryDao;
import com.maboy.applicantmarket.matching.dao.dto.MatchingExplanationDto;
import com.maboy.applicantmarket.matching.dao.dto.MatchingQueryDto;
import com.maboy.applicantmarket.matching.model.MatchExplanation;
import com.maboy.applicantmarket.matching.model.RankFactors;
import com.maboy.applicantmarket.matching.model.RankedCandidate;
import com.maboy.applicantmarket.matching.model.ResolvedCategory;
import com.maboy.applicantmarket.matching.model.request.CandidateSearchRequest;
import com.maboy.applicantmarket.matching.model.response.CandidateCardResponse;
import com.maboy.applicantmarket.matching.model.response.CandidateSearchResponse;
import com.maboy.applicantmarket.matching.model.response.CategorySummaryResponse;
import com.maboy.applicantmarket.matching.model.response.MatchReasonResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final MatchingProperties properties;
    private final ApplicantModuleApi applicantModuleApi;
    private final CandidateCategoryService categoryService;
    private final RankingService rankingService;
    private final ExplanationService explanationService;
    private final MatchingQueryDao queryDao;
    private final MatchingExplanationDao explanationDao;
    private final CandidateSearchConverter converter;

    @Transactional
    public CandidateSearchResponse search(UUID employerId, CandidateSearchRequest request) {
        validate(request);

        // 1. Найти всех кандидатов в требуемой категории
        List<UUID> applicantIds = categoryService.findApplicantsInCategory(
                request.getSpecializationId(), request.getGradeId());

        // 2. Резолв категорий батчем
        Map<UUID, ResolvedCategory> categories = categoryService.resolveAll(applicantIds);
        Map<UUID, ApplicantSummary> summaries = applicantModuleApi.getSummaries(applicantIds);
        // Фильтр кандидатов по настройкам приватности (видимость в поиске)
        applicantIds = applicantIds.stream().filter(id -> summaries.get(id).isVisibleInSearch()).collect(Collectors.toList());
        // 3. Ранжирование
        List<RankedCandidate> ranked = new ArrayList<>();
        for (UUID applicantId : applicantIds) {
            ResolvedCategory cat = categories.get(applicantId);
            if (cat == null) {
                continue;
            }
            RankedCandidate rc = rankingService.rank(applicantId, request, cat);
            rc.setExplanations(explanationService.explain(rc.getFactors()));
            ranked.add(rc);
        }

        // 5. Фильтр по minMatchLevel
        double minMatch = request.getMinMatchLevel() != null
                ? request.getMinMatchLevel()
                : properties.getSearch().getDefaultMinMatchLevel();
        ranked = ranked.stream()
                .filter(r -> r.getFactors().getMatchLevel() >= minMatch)
                .collect(Collectors.toList());

        // 6. Сортировка
        Comparator<RankedCandidate> cmp = switch (request.getSortBy() == null ? "RANK" : request.getSortBy()) {
            case "MATCH_LEVEL" -> Comparator
                    .comparingDouble((RankedCandidate r) -> r.getFactors().getMatchLevel()).reversed()
                    .thenComparingDouble(RankedCandidate::getRankScore).reversed();
            case "FRESHNESS" -> Comparator
                    .comparingDouble((RankedCandidate r) -> r.getFactors().getFreshnessFactor()).reversed()
                    .thenComparingDouble(RankedCandidate::getRankScore).reversed();
            case "FSP" -> Comparator
                    .comparingDouble((RankedCandidate r) -> r.getFactors().getFspBoost()).reversed()
                    .thenComparingDouble(RankedCandidate::getRankScore).reversed();
            case "ASSESSMENT_SCORE" -> Comparator
                    .comparingDouble((RankedCandidate r) ->
                            r.getFactors().getAssessmentScore() == null ? 0.0 : r.getFactors().getAssessmentScore())
                    .reversed()
                    .thenComparingDouble(RankedCandidate::getRankScore).reversed();
            default -> Comparator
                    .comparingDouble(RankedCandidate::getRankScore).reversed();
        };
        ranked.sort(cmp);

        // 7. Пагинация
        int page = request.getPage() == null ? 0 : request.getPage();
        int size = clamp(request.getSize(), 1, properties.getSearch().getMaxPageSize());
        int from = page * size;
        int to = Math.min(from + size, ranked.size());
        List<RankedCandidate> pageContent = from >= ranked.size()
                ? List.of()
                : ranked.subList(from, to);

        // 8. Сохранить query + explanations
        MatchingQueryDto savedQuery = queryDao.save(MatchingQueryDto.builder()
                .employerId(employerId)
                .paramsJson(toJson(request))
                .resultCount(ranked.size())
                .page(page)
                .pageSize(size)
                .build());
        persistExplanations(savedQuery.getId(), ranked);

        // 9. Собрать ответ
        return buildResponse(savedQuery.getId(), pageContent, ranked.size(), page, size);
    }

    public List<CategorySummaryResponse> getCategorySummary() {
        return categoryService.getCategorySummary();
    }

    public CandidateCardResponse getCandidateCard(UUID applicantId) {
        ResolvedCategory cat = categoryService.resolve(applicantId)
            .orElseThrow(() -> new MatchingException("Candidate not categorised: " + applicantId));

        // Создаём пустой запрос для ранжирования «без требований»
        CandidateSearchRequest request = new CandidateSearchRequest();
        request.setSpecializationId(cat.getSpecializationId());
        request.setGradeId(cat.getPrimaryGradeId());

        RankedCandidate rc = rankingService.rank(applicantId, request, cat);
        rc.setExplanations(explanationService.explain(rc.getFactors()));
        return converter.toCard(rc);
    }

    public CandidateSearchResponse replayQuery(UUID employerId, UUID queryId) {
        MatchingQueryDto query = queryDao.findByIdAndEmployerId(queryId, employerId)
            .orElseThrow(() -> MatchingQueryNotFoundException.byId(queryId));

        List<MatchingExplanationDto> explanations = explanationDao
            .findAllByQueryIdOrderByRankPositionAsc(queryId);

        List<CandidateCardResponse> items = explanations.stream()
            .map(e -> converter.explanationToCard(e))
            .toList();

        return CandidateSearchResponse.builder()
            .queryId(query.getId())
            .page(query.getPage())
            .size(query.getPageSize())
            .total(query.getResultCount())
            .totalPages(query.getPageSize() == 0 ? 0
                : (int) Math.ceil((double) query.getResultCount() / query.getPageSize()))
            .items(items)
            .build();
    }

    private void validate(CandidateSearchRequest request) {
        if (request == null) {
            throw new InvalidSearchRequestException("Request is required");
        }
        if (request.getSpecializationId() == null) {
            throw new InvalidSearchRequestException("specializationId is required");
        }
        if (request.getGradeId() == null) {
            throw new InvalidSearchRequestException("gradeId is required");
        }
        if (request.getSortBy() != null) {
            Set<String> allowed = Set.of("RANK", "MATCH_LEVEL", "FRESHNESS", "FSP", "ASSESSMENT_SCORE");
            if (!allowed.contains(request.getSortBy())) {
                throw new InvalidSearchRequestException("Unknown sortBy: " + request.getSortBy());
            }
        }
        if (request.getMinMatchLevel() != null
            && (request.getMinMatchLevel() < 0 || request.getMinMatchLevel() > 1)) {
            throw new InvalidSearchRequestException("minMatchLevel must be in [0, 1]");
        }
    }

    private int clamp(Integer value, int min, int max) {
        if (value == null) return properties.getSearch().getDefaultPageSize();
        return Math.max(min, Math.min(max, value));
    }

    private Map<String, Object> toJson(CandidateSearchRequest request) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("specializationId", request.getSpecializationId());
        map.put("gradeId", request.getGradeId());
        map.put("skillIds", request.getSkillIds());
        map.put("minMatchLevel", request.getMinMatchLevel());
        map.put("sortBy", request.getSortBy());
        map.put("page", request.getPage());
        map.put("size", request.getSize());
        return map;
    }

    private void persistExplanations(UUID queryId, List<RankedCandidate> ranked) {
        if (ranked.isEmpty()) return;

        List<MatchingExplanationDto> entities = new ArrayList<>(ranked.size());
        for (int i = 0; i < ranked.size(); i++) {
            RankedCandidate rc = ranked.get(i);
            entities.add(MatchingExplanationDto.builder()
                .queryId(queryId)
                .applicantId(rc.getApplicantId())
                .rankPosition(i + 1)
                .rankScore(BigDecimal.valueOf(rc.getRankScore())
                    .setScale(5, RoundingMode.HALF_UP))
                .matchLevel(BigDecimal.valueOf(rc.getMatchLevel())
                    .setScale(5, RoundingMode.HALF_UP))
                .reasonsJson(explanationToMap(rc.getExplanations()))
                .factorsJson(factorsToMap(rc.getFactors()))
                .build());
        }
        explanationDao.saveAll(entities);
    }

    private Map<String, Object> explanationToMap(List<MatchExplanation> list) {
        List<Map<String, Object>> items = list.stream()
            .map(e -> Map.<String, Object>of(
                "code", e.getCode(),
                "text", e.getText(),
                "weight", e.getWeight()))
            .toList();
        return Map.of("reasons", items);
    }

    private Map<String, Object> factorsToMap(RankFactors f) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("baseScore", f.getBaseScore());
        map.put("fspBoost", f.getFspBoost());
        map.put("skillMatch", f.getSkillMatch());
        map.put("gradeMatch", f.getGradeMatch());
        map.put("matchLevel", f.getMatchLevel());
        map.put("freshnessFactor", f.getFreshnessFactor());
        map.put("rankScore", f.getRankScore());
        map.put("assessmentScore", f.getAssessmentScore());
        map.put("fspAchievementCount", f.getFspAchievementCount());
        map.put("matchedSkillCount", f.getMatchedSkillIds() == null ? 0 : f.getMatchedSkillIds().size());
        map.put("partialSkillCount", f.getPartialSkillIds() == null ? 0 : f.getPartialSkillIds().size());
        map.put("missingSkillCount", f.getMissingSkillIds() == null ? 0 : f.getMissingSkillIds().size());
        return map;
    }

    private CandidateSearchResponse buildResponse(UUID queryId,
        List<RankedCandidate> pageContent,
        int total,
        int page,
        int size) {
        List<CandidateCardResponse> items = pageContent.stream()
            .map(this::toCard)
            .toList();

        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) total / size);

        return CandidateSearchResponse.builder()
            .queryId(queryId)
            .page(page)
            .size(size)
            .total(total)
            .totalPages(totalPages)
            .items(items)
            .suggestion(total == 0 ? buildSuggestion() : null)
            .build();
    }

    private CandidateCardResponse toCard(RankedCandidate rc) {
        RankFactors f = rc.getFactors();
        return CandidateCardResponse.builder()
            .applicantId(rc.getApplicantId())
            .displayName(rc.getDisplayName())
            .city(rc.getCity())
            .experienceYears(rc.getExperienceYears())
            .primarySkillId(rc.getPrimarySkillId())
            .primarySkillName(rc.getPrimarySkillName())
            .primaryGradeId(rc.getPrimaryGradeId())
            .primaryGradeName(rc.getPrimaryGradeName())
            .categoryId(rc.getCategoryId())
            .categoryName(rc.getCategoryName())
            .assessmentScore(f.getAssessmentScore())
            .fspAchievementCount(rc.getFspAchievementCount())
            .matchLevel(rc.getMatchLevel())
            .rankScore(rc.getRankScore())
            .reasons(rc.getExplanations().stream()
                .map(e -> MatchReasonResponse.builder()
                    .code(e.getCode())
                    .text(e.getText())
                    .weight(e.getWeight())
                    .build())
                .toList())
            .build();
    }

    private String buildSuggestion() {
        return "Нет кандидатов, соответствующих запросу. "
               + "Попробуйте снизить требования или расширить набор навыков.";
    }
}