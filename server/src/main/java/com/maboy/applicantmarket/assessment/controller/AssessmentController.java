package com.maboy.applicantmarket.assessment.controller;

import com.maboy.applicantmarket.assessment.converter.AssessmentItemConverter;
import com.maboy.applicantmarket.assessment.converter.AssessmentSessionConverter;
import com.maboy.applicantmarket.assessment.dao.AssessmentAnswerDao;
import com.maboy.applicantmarket.assessment.model.AssessmentItem;
import com.maboy.applicantmarket.assessment.model.AssessmentSession;
import com.maboy.applicantmarket.assessment.model.request.StartAssessmentRequest;
import com.maboy.applicantmarket.assessment.model.request.SubmitAnswerRequest;
import com.maboy.applicantmarket.assessment.model.response.*;
import com.maboy.applicantmarket.assessment.service.AssessmentSessionService;
import com.maboy.applicantmarket.assessment.service.grading.GradingOutcome;
import com.maboy.applicantmarket.auth.config.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/assessment")
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentSessionService sessionService;
    private final AssessmentSessionConverter sessionConverter;
    private final AssessmentItemConverter itemConverter;
    private final AssessmentAnswerDao answerDao;

    // ============================================================
    // START SESSION
    // ============================================================
    @PostMapping("/sessions")
    public AssessmentSessionResponse startSession(
        @CurrentUser UUID userId,
        @RequestBody StartAssessmentRequest request
    ) {
        AssessmentSession session = sessionService.startSession(
            userId, request.getSkillId(), request.getClaimedGradeId());

        List<AssessmentItemResponse> items = buildItemResponses(session.getId());
        return sessionConverter.toResponse(session, items);
    }

    // ============================================================
    // GET SESSION
    // ============================================================
    @GetMapping("/sessions/{id}")
    public AssessmentSessionResponse getSession(
        @CurrentUser UUID userId,
        @PathVariable UUID id
    ) {
        AssessmentSession session = sessionService.getSession(userId, id);
        List<AssessmentItemResponse> items = buildItemResponses(session.getId());
        return sessionConverter.toResponse(session, items);
    }

    // ============================================================
    // SUBMIT ANSWER
    // ============================================================
    @PostMapping("/sessions/{id}/answers")
    public void submitAnswer(
        @CurrentUser UUID userId,
        @PathVariable UUID id,
        @RequestBody SubmitAnswerRequest request
    ) {
        sessionService.submitAnswer(userId, id, request.getItemId(), request.getAnswer());
    }

    // ============================================================
    // COMPLETE SESSION
    // ============================================================
    @PostMapping("/sessions/{id}/complete")
    public AssessmentResultResponse completeSession(
        @CurrentUser UUID userId,
        @PathVariable UUID id
    ) {
        GradingOutcome outcome = sessionService.completeSession(userId, id);
        return AssessmentResultResponse.builder()
            .sessionId(id)
            .status(outcome.status())
            .score(outcome.score())
            .resultGradeId(outcome.resultGradeId())
            .build();
    }

    // ============================================================
    // HISTORY
    // ============================================================
    @GetMapping("/sessions/history")
    public List<AssessmentHistoryItemResponse> history(
        @CurrentUser UUID userId
    ) {
        List<AssessmentHistoryItemResponse> result = sessionService.getHistory(userId).stream()
            .map(s -> AssessmentHistoryItemResponse.builder()
                .sessionId(s.getId())
                .skillId(s.getSkillId())
                .claimedGradeId(s.getClaimedGradeId())
                .resultGradeId(s.getResultGradeId())
                .status(s.getStatus())
                .score(s.getScore())
                .startedAt(s.getStartedAt())
                .completedAt(s.getCompletedAt())
                .build())
            .toList();
        return result;
    }

    // ============================================================
    // HELPERS
    // ============================================================

    /**
     * Загружает items сессии и помечает отвеченные. correctAnswer в response не попадает никогда.
     */
    private List<AssessmentItemResponse> buildItemResponses(UUID sessionId) {
        List<AssessmentItem> items = sessionService.loadItems(sessionId);
        if (items.isEmpty()) {
            return List.of();
        }

        List<UUID> itemIds = items.stream().map(AssessmentItem::getId).toList();
        Set<UUID> answeredIds = answerDao.findAllByItemIdIn(itemIds).stream()
            .map(a -> a.getItemId())
            .collect(Collectors.toSet());

        List<AssessmentItemResponse> result = new ArrayList<>(items.size());
        for (AssessmentItem item : items) {
            result.add(itemConverter.toResponse(item, answeredIds.contains(item.getId())));
        }
        return result;
    }
}