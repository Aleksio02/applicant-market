package com.maboy.applicantmarket.interaction.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateInvitationRequest {

    @NotNull(message = "Candidate must not be null")
    private UUID candidateId;

    private UUID vacancyId;

    @Size(max = 255)
    private String title;

    private String message;

    private Long salaryFrom;
    private Long salaryTo;
}