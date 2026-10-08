package com.maboy.applicantmarket.applicant.config;

import com.maboy.applicantmarket.applicant.api.event.AssessmentCompleted;
import com.maboy.applicantmarket.applicant.service.GradeChangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Configuration
@RequiredArgsConstructor
public class ApplicantEventsConfig {

    private final GradeChangeService gradeChangeService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAssessmentCompleted(AssessmentCompleted event) {
        gradeChangeService.applyAssessmentResult(
                event.applicantId(),
                event.skillId(),
                event.gradeId()
        );
    }
}