package com.maboy.applicantmarket.matching.service;

import com.maboy.applicantmarket.applicant.api.ApplicantModuleApi;
import com.maboy.applicantmarket.applicant.api.model.ApplicantSummary;
import com.maboy.applicantmarket.commons.dao.CategoryDao;
import com.maboy.applicantmarket.commons.dao.GradeDao;
import com.maboy.applicantmarket.commons.dao.SkillDao;
import com.maboy.applicantmarket.commons.dao.SpecializationDao;
import com.maboy.applicantmarket.commons.dao.dto.CategoryDto;
import com.maboy.applicantmarket.commons.dao.dto.GradeDto;
import com.maboy.applicantmarket.commons.dao.dto.SkillDto;
import com.maboy.applicantmarket.commons.dao.dto.SpecializationDto;
import com.maboy.applicantmarket.matching.model.ResolvedCategory;
import com.maboy.applicantmarket.matching.model.response.CategorySummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateCategoryService {

    private final ApplicantModuleApi applicantModuleApi;
    private final SkillDao skillDao;
    private final SpecializationDao specializationDao;
    private final GradeDao gradeDao;
    private final CategoryDao categoryDao;

    @Transactional(readOnly = true)
    public Optional<ResolvedCategory> resolve(UUID applicantId) {
        return resolveAll(List.of(applicantId)).values().stream().findFirst();
    }

    @Transactional(readOnly = true)
    public Map<UUID, ResolvedCategory> resolveAll(Collection<UUID> applicantIds) {
        if (applicantIds == null || applicantIds.isEmpty()) {
            return Map.of();
        }

        // 1. Один запрос за summary всех кандидатов
        Map<UUID, ApplicantSummary> summaries = applicantModuleApi.getSummaries(applicantIds);

        // 2. Собрать все требуемые skillId, specializationId, gradeId
        Set<UUID> skillIds = new HashSet<>();
        Set<UUID> gradeIds = new HashSet<>();
        for (ApplicantSummary s : summaries.values()) {
            if (s.getPrimarySkillId() != null) skillIds.add(s.getPrimarySkillId());
            if (s.getPrimaryGradeId() != null) gradeIds.add(s.getPrimaryGradeId());
        }
        if (skillIds.isEmpty() || gradeIds.isEmpty()) {
            return Map.of();
        }

        // 3. Один запрос за справочниками
        Map<UUID, SkillDto> skills = skillDao.findAllById(skillIds).stream()
                .collect(Collectors.toMap(SkillDto::getId, s -> s));
        Map<UUID, GradeDto> grades = gradeDao.findAllById(gradeIds).stream()
                .collect(Collectors.toMap(GradeDto::getId, g -> g));

        // Специализации по code (skills.category == specialization.code)
        Set<String> specCodes = skills.values().stream()
                .map(SkillDto::getCategory).collect(Collectors.toSet());
        Map<String, SpecializationDto> specsByCode = specializationDao.findAll().stream()
                .filter(s -> specCodes.contains(s.getCode()))
                .collect(Collectors.toMap(SpecializationDto::getCode, s -> s));

        // 4. Собрать категории одним запросом
        // Пара (specializationId, gradeId) — заранее известный маппинг. Ходим по всем категориям,
        // их не больше 30 (6 специализаций × 5 грейдов) — забираем всё и фильтруем в памяти.
        Map<String, CategoryDto> categoriesByKey = categoryDao.findAll().stream()
                .collect(Collectors.toMap(
                        c -> c.getSpecializationId() + "|" + c.getGradeId(),
                        c -> c
                ));

        // 5. Собрать ResolvedCategory
        Map<UUID, ResolvedCategory> result = new HashMap<>();
        for (Map.Entry<UUID, ApplicantSummary> e : summaries.entrySet()) {
            UUID applicantId = e.getKey();
            ApplicantSummary s = e.getValue();
            if (s.getPrimarySkillId() == null || s.getPrimaryGradeId() == null) continue;

            SkillDto skill = skills.get(s.getPrimarySkillId());
            GradeDto grade = grades.get(s.getPrimaryGradeId());
            if (skill == null || grade == null) continue;

            SpecializationDto spec = specsByCode.get(skill.getCategory());
            if (spec == null) continue;

            String key = spec.getId() + "|" + grade.getId();
            CategoryDto cat = categoriesByKey.get(key);
            if (cat == null) continue;

            result.put(applicantId, ResolvedCategory.builder()
                    .applicantId(applicantId)
                    .primarySkillId(skill.getId())
                    .primaryGradeId(grade.getId())
                    .gradeLevel(grade.getLevel())
                    .specializationId(spec.getId())
                    .categoryId(cat.getId())
                    .categoryCode(cat.getCode())
                    .categoryName(cat.getName())
                    .build());
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<UUID> findApplicantsInCategory(UUID specializationId, UUID gradeId) {
        CategoryDto cat = categoryDao.findBySpecializationIdAndGradeId(specializationId, gradeId)
                .orElse(null);
        if (cat == null) {
            return List.of();
        }
        // Здесь вся магия: candidate-проекции нет, значит идём по профилям всех кандидатов
        // и фильтруем через resolveAll. Для MVP с 100–200 кандидатами это ок.
        return applicantModuleApi.getApplicantIdsByPrimarySkillCategoryAndGrade(
                specializationId, gradeId);
    }

    @Transactional(readOnly = true)
    public List<CategorySummaryResponse> getCategorySummary() {
        List<CategoryDto> categories = categoryDao.findAllByIsActiveTrue();

        List<CategorySummaryResponse> result = new ArrayList<>(categories.size());
        for (CategoryDto c : categories) {
            long count = applicantModuleApi.countApplicantsInCategory(
                c.getSpecializationId(), c.getGradeId());
            result.add(CategorySummaryResponse.builder()
                .categoryId(c.getId())
                .categoryCode(c.getCode())
                .categoryName(c.getName())
                .specializationId(c.getSpecializationId())
                .gradeId(c.getGradeId())
                .candidateCount(count)
                .build());
        }
        return result;
    }
}