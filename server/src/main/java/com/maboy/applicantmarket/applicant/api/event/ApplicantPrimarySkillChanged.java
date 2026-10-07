package com.maboy.applicantmarket.applicant.api.event;

import java.util.UUID;

public record ApplicantPrimarySkillChanged(UUID applicantId, UUID skillId, UUID gradeId) {
}
