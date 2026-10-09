package com.maboy.applicantmarket.matching.service;

import java.util.UUID;

public interface AssessmentScoreProvider {
    Double getLastScore(UUID applicantId, UUID skillId);
}