package com.maboy.applicantmarket.assignment.model;

import com.maboy.applicantmarket.assignment.model.enums.AssignmentAttemptStatus;
import com.maboy.applicantmarket.assignment.model.enums.EvaluationVerdict;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class AssignmentAttempt {
    private UUID id;
    private UUID assignmentId;
    private UUID candidateId;
    private AssignmentAttemptStatus status;
    private Instant startedAt;
    private Instant deadlineAt;
    private Instant submittedAt;
    private String contentText;
    private Instant evaluatedAt;
    private Integer score;
    private EvaluationVerdict verdict;
    private String feedback;
    private Instant createdAt;
    private Instant updatedAt;
}