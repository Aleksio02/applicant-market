package com.maboy.applicantmarket.assignment.controller;

import com.maboy.applicantmarket.assignment.model.AssignmentAttempt;
import com.maboy.applicantmarket.assignment.model.request.EvaluateAttemptRequest;
import com.maboy.applicantmarket.assignment.model.request.SubmitAttemptRequest;
import com.maboy.applicantmarket.assignment.service.AssignmentAttemptService;
import com.maboy.applicantmarket.auth.config.annotation.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assignment/attempt")
public class AssignmentAttemptController {

    private final AssignmentAttemptService attemptService;

    public AssignmentAttemptController(AssignmentAttemptService attemptService) {
        this.attemptService = attemptService;
    }

    @PostMapping("/assignment/{assignmentId}/start")
    public AssignmentAttempt start(@PathVariable UUID assignmentId,
                                   @CurrentUser UUID candidateId) {
        return attemptService.start(candidateId, assignmentId);
    }

    @PatchMapping("/{attemptId}/submit")
    public AssignmentAttempt submit(@PathVariable UUID attemptId,
                                    @Valid @RequestBody SubmitAttemptRequest request,
                                    @CurrentUser UUID candidateId) {
        return attemptService.submit(candidateId, attemptId, request);
    }

    @GetMapping("/{attemptId}")
    public AssignmentAttempt getById(@PathVariable UUID attemptId,
                                     @CurrentUser UUID requesterId) {
        return attemptService.getById(requesterId, attemptId);
    }

    @GetMapping("/mine")
    public List<AssignmentAttempt> getMine(@CurrentUser UUID candidateId) {
        return attemptService.getMine(candidateId);
    }

    @GetMapping("/assignment/{assignmentId}")
    public List<AssignmentAttempt> getByAssignment(@PathVariable UUID assignmentId,
                                                   @CurrentUser UUID ownerId) {
        return attemptService.getByAssignment(ownerId, assignmentId);
    }

    @PatchMapping("/{attemptId}/evaluate")
    public AssignmentAttempt evaluate(@PathVariable UUID attemptId,
                                      @Valid @RequestBody EvaluateAttemptRequest request,
                                      @CurrentUser UUID ownerId) {
        return attemptService.evaluate(ownerId, attemptId, request);
    }
}