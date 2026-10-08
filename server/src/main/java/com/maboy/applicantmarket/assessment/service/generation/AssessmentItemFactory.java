package com.maboy.applicantmarket.assessment.service.generation;

import com.maboy.applicantmarket.assessment.model.AssessmentItem;
import com.maboy.applicantmarket.assessment.model.AssessmentTemplate;
import java.util.UUID;

public interface AssessmentItemFactory {

    AssessmentItem generate(AssessmentTemplate template, int position, long itemSeed);
}