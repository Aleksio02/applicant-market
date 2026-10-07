package com.maboy.applicantmarket.applicant.api.event;

import java.util.UUID;

public record ApplicantPrivacyChanged(UUID applicantId, boolean visibleInSearch) {
}
