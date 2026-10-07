package com.maboy.applicantmarket.vacancy.model.request;

import com.maboy.applicantmarket.commons.model.enums.WorkFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
public class UpdateVacancyRequest {
    private Optional<String> title = Optional.empty();
    private Optional<String> description = Optional.empty();
    private Optional<UUID> specializationId = Optional.empty();
    private Optional<UUID> gradeId = Optional.empty();
    private Optional<Long> salaryFrom = Optional.empty();
    private Optional<Long> salaryTo = Optional.empty();
    private Optional<WorkFormat> format = Optional.empty();
    private Optional<String> location = Optional.empty();
}