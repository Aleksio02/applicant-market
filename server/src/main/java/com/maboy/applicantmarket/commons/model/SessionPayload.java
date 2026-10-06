package com.maboy.applicantmarket.commons.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class SessionPayload {

    private UUID userId;
    private Instant created;
    private Instant expires;
    private User currentUser;

    public SessionPayload() {
    }

    public SessionPayload(UUID userId) {
        this.userId = userId;
        this.created = Instant.now();
        this.expires = Instant.now().plusSeconds(3600);
    }

    public SessionPayload(UUID userId, Instant created, Instant expires) {
        this.userId = userId;
        this.created = created;
        this.expires = expires;
    }
}