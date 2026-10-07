package com.maboy.applicantmarket.applicant.model;

import java.time.Instant;
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
public class ApplicantPrivacySettings {
    private UUID applicantId;
    private Boolean visibleInSearch;
    private Boolean allowInvitations;
    private Boolean showContactsAfterAccept;
    private Boolean showFspAchievements;
    private Instant updatedAt;
}