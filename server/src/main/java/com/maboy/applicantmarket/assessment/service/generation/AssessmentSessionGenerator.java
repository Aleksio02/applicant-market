package com.maboy.applicantmarket.assessment.service.generation;

import com.maboy.applicantmarket.assessment.model.AssessmentItem;
import java.util.List;
import java.util.UUID;

public interface AssessmentSessionGenerator {
    List<AssessmentItem> generateFor(UUID skillId, UUID claimedGradeId);
}