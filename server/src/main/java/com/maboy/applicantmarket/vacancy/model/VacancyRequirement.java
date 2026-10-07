package com.maboy.applicantmarket.vacancy.model;

import com.maboy.applicantmarket.commons.model.Skill;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class VacancyRequirement {
    private UUID id;
    private UUID vacancyId;
    private UUID skillId;
    private Skill skill;
    private short level;
    private boolean mandatory;
    private Instant createdAt;
    private Instant updatedAt;
}