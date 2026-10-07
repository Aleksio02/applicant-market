package com.maboy.applicantmarket.applicant.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FspAchievementStub {
    private UUID id;
    private UUID applicantId;
    private String eventName;
    private LocalDate eventDate;
    private Integer place;
    private String category;
    private Boolean verified;
    private String source;
    private Instant createdAt;
}