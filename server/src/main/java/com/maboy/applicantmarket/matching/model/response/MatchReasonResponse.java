package com.maboy.applicantmarket.matching.model.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchReasonResponse {
    private String code;
    private String text;
    private double weight;
}