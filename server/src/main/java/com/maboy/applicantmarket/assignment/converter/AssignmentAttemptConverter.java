package com.maboy.applicantmarket.assignment.converter;

import com.maboy.applicantmarket.assignment.dao.dto.AssignmentAttemptDto;
import com.maboy.applicantmarket.assignment.model.AssignmentAttempt;

public class AssignmentAttemptConverter {
    public void fromDto(AssignmentAttemptDto source, AssignmentAttempt destination) {
        destination.setId(source.getId());
        if (source.getAssignment() != null) {
            destination.setAssignmentId(source.getAssignment().getId());
        }
        destination.setCandidateId(source.getCandidateId());
        destination.setStatus(source.getStatus());
        destination.setStartedAt(source.getStartedAt());
        destination.setDeadlineAt(source.getDeadlineAt());
        destination.setSubmittedAt(source.getSubmittedAt());
        destination.setContentText(source.getContentText());
        destination.setEvaluatedAt(source.getEvaluatedAt());
        destination.setScore(source.getScore());
        destination.setVerdict(source.getVerdict());
        destination.setFeedback(source.getFeedback());
        destination.setCreatedAt(source.getCreatedAt());
        destination.setUpdatedAt(source.getUpdatedAt());
    }
}