package com.maboy.applicantmarket.interaction.model;

import com.maboy.applicantmarket.interaction.model.enums.InteractionStatus;
import com.maboy.applicantmarket.interaction.model.enums.InteractionType;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class Interaction {
    private UUID id;
    private InteractionType type;
    private UUID employerId;
    private UUID candidateId;
    private UUID vacancyId;
    private String title;
    private String message;
    private Long salaryFrom;
    private Long salaryTo;
    private InteractionStatus status;
    private Instant contactsRevealedAt;
    private Instant sentAt;
    private Instant viewedAt;
    private Instant respondedAt;
    private Instant createdAt;
    private Instant updatedAt;
}