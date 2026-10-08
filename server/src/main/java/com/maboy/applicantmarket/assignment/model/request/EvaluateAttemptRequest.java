package com.maboy.applicantmarket.assignment.model.request;

import com.maboy.applicantmarket.assignment.model.enums.EvaluationVerdict;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EvaluateAttemptRequest {

    @NotNull(message = "Verdict must not be null")
    private EvaluationVerdict verdict;

    private Integer score;

    private String feedback;
}