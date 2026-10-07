package com.maboy.applicantmarket.vacancy.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class UpdateVacancyRequirementRequest {
    private Optional<Short> level = Optional.empty();
    private Optional<Boolean> mandatory = Optional.empty();
}