package com.maboy.applicantmarket.matching.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchExplanation {
    private String code;
    private String text;
    private double weight;
}