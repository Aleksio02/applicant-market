package com.maboy.applicantmarket.applicant.config;

import com.maboy.applicantmarket.applicant.api.event.AssessmentCompleted;
import com.maboy.applicantmarket.applicant.service.GradeChangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicantEventsConfig {

    private final GradeChangeService gradeChangeService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAssessmentCompleted(AssessmentCompleted event) {
        log.info("Received AssessmentCompleted: applicant={}, skill={}, grade={}, score={}",
            event.applicantId(), event.skillId(), event.gradeId(), event.score());
        try {
            gradeChangeService.applyAssessmentResult(
                event.applicantId(), event.skillId(), event.gradeId());
            log.info("Applied assessment result: applicant={}, skill={}, grade={}",
                event.applicantId(), event.skillId(), event.gradeId());
        } catch (RuntimeException e) {
            log.error("Failed to apply assessment result: applicant={}, skill={}, grade={}",
                event.applicantId(), event.skillId(), event.gradeId(), e);
        }
    }
}