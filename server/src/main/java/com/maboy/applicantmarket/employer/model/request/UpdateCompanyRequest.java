package com.maboy.applicantmarket.employer.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class UpdateCompanyRequest {
    private Optional<String> name = Optional.empty();
    private Optional<String> description = Optional.empty();
    private Optional<String> industry = Optional.empty();
    private Optional<String> website = Optional.empty();
    private Optional<String> contactPersonName = Optional.empty();
    private Optional<String> contactPersonPosition = Optional.empty();
    private Optional<String> contactEmail = Optional.empty();
    private Optional<String> contactPhone = Optional.empty();
    private Optional<String> logoUrl = Optional.empty();
}