package com.maboy.applicantmarket.applicant.model;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicantProfile {
    private UUID id;
    private UUID userId;
    private String firstName;
    private String lastName;
    private String middleName;
    private String phone;
    private String city;
    private String country;
    private String about;
    private Short experienceYears;
    private String fspId;
    private Instant fspLinkedAt;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;

    public boolean hasFspLink() {
        return fspId != null;
    }

    public void linkFsp(String fspId) {
        this.fspId = fspId;
        this.fspLinkedAt = Instant.now();
    }

    public void unlinkFsp() {
        this.fspId = null;
        this.fspLinkedAt = null;
    }

    public void changeStatus(String newStatus) {
        if (!"DRAFT".equals(newStatus) && !"ACTIVE".equals(newStatus) && !"HIDDEN".equals(newStatus)) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }
        this.status = newStatus;
    }
}