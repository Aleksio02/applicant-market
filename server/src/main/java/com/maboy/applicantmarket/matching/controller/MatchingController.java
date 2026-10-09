package com.maboy.applicantmarket.matching.controller;

import com.maboy.applicantmarket.auth.config.annotation.CurrentUser;
import com.maboy.applicantmarket.matching.model.request.CandidateSearchRequest;
import com.maboy.applicantmarket.matching.model.response.CandidateCardResponse;
import com.maboy.applicantmarket.matching.model.response.CandidateSearchResponse;
import com.maboy.applicantmarket.matching.model.response.CategorySummaryResponse;
import com.maboy.applicantmarket.matching.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    /**
     * Поиск кандидатов по описанию потребности.
     */
    @PostMapping("/search")
    public CandidateSearchResponse search(
        @CurrentUser UUID employerId,
        @RequestBody CandidateSearchRequest request
    ) {
        return matchingService.search(employerId, request);
    }

    /**
     * Сводка по категориям: сколько кандидатов в каждой. Без пагинации, это агрегат.
     */
    @GetMapping("/categories")
    public List<CategorySummaryResponse> categories(@CurrentUser UUID employerId) {
        return matchingService.getCategorySummary();
    }

    /**
     * Карточка кандидата без контактов.
     */
    @GetMapping("/candidates/{applicantId}")
    public CandidateCardResponse candidate(
        @CurrentUser UUID employerId,
        @PathVariable UUID applicantId
    ) {
        return matchingService.getCandidateCard(applicantId);
    }

    /**
     * Повторный просмотр выдачи по queryId.
     */
    @GetMapping("/queries/{queryId}")
    public ResponseEntity<CandidateSearchResponse> replayQuery(
        @CurrentUser UUID employerId,
        @PathVariable UUID queryId
    ) {
        return ResponseEntity.ok(matchingService.replayQuery(employerId, queryId));
    }
}