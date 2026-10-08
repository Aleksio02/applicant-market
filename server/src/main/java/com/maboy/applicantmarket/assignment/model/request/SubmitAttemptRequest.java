package com.maboy.applicantmarket.assignment.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitAttemptRequest {

    @NotBlank(message = "Content must not be empty")
    private String contentText;
}