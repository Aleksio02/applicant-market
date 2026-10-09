package com.maboy.applicantmarket.matching.model.response;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateSearchResponse {

    private UUID queryId;
    private int page;
    private int size;
    private long total;
    private int totalPages;
    private List<CandidateCardResponse> items;
    private String suggestion;
}