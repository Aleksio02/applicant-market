package com.maboy.applicantmarket.assignment.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class UpdateAssignmentRequest {
    private Optional<String> title = Optional.empty();
    private Optional<String> description = Optional.empty();
    private Optional<Integer> durationHours = Optional.empty();
    private Optional<Boolean> active = Optional.empty();
}