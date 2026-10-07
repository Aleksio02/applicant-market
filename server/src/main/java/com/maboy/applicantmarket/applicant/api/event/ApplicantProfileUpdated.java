package com.maboy.applicantmarket.applicant.api.event;

import java.util.UUID;

public record ApplicantProfileUpdated(UUID applicantId, java.util.Set<String> changedFields) {
}
