package com.maboy.applicantmarket.applicant.model;

import java.time.Instant;
import java.util.Map;
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
public class ApplicantResume {
    private UUID id;
    private UUID applicantId;
    private String fileUrl;
    private String fileName;
    private String contentType;
    private Long sizeBytes;
    private Map<String, Object> parsedJson;
    private Boolean isPrimary;
    private Instant uploadedAt;
}