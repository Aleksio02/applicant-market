package com.maboy.applicantmarket.auth.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ConfirmEmailRequest {

    @NotNull(message = "UserId must not be null")
    private UUID userId;

    @NotBlank(message = "Code must not be empty")
    private String code;
}