package com.maboy.applicantmarket.assignment.service;

import com.maboy.applicantmarket.assignment.model.AssignmentAttempt;
import com.maboy.applicantmarket.assignment.model.request.EvaluateAttemptRequest;
import com.maboy.applicantmarket.assignment.model.request.SubmitAttemptRequest;

import java.util.List;
import java.util.UUID;

public interface AssignmentAttemptService {
    AssignmentAttempt start(UUID candidateId, UUID assignmentId);
    AssignmentAttempt submit(UUID candidateId, UUID attemptId, SubmitAttemptRequest request);
    AssignmentAttempt getById(UUID requesterId, UUID attemptId);
    List<AssignmentAttempt> getMine(UUID candidateId);
    List<AssignmentAttempt> getByAssignment(UUID ownerId, UUID assignmentId);
    AssignmentAttempt evaluate(UUID ownerId, UUID attemptId, EvaluateAttemptRequest request);
}