package com.maboy.applicantmarket.applicant.api.event;

import java.util.UUID;

public record ApplicantProfileCreated(UUID applicantId, UUID userId) {
}
