package com.maboy.applicantmarket.applicant.api.event;

import java.util.UUID;

public record FspIdLinked(UUID applicantId, String fspId) {
}
