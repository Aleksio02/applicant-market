package com.maboy.applicantmarket.applicant.api.event;

import java.time.Instant;
import java.util.UUID;

public record ApplicantSkillGradeChanged(UUID applicantId,
                                         UUID skillId,
                                         UUID oldGradeId,
                                         UUID newGradeId,
                                         Instant changedAt) {
}
