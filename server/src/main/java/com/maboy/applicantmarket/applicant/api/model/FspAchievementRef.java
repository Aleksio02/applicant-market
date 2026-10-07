package com.maboy.applicantmarket.applicant.api.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FspAchievementRef {
    private String eventName;
    private LocalDate eventDate;
    private Integer place;
    private String category;
}