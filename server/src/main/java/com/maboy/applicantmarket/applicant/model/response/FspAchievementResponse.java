package com.maboy.applicantmarket.applicant.model.response;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FspAchievementResponse {
    private UUID id;
    private String eventName;
    private LocalDate eventDate;
    private Integer place;
    private String category;
    private boolean verified;
}