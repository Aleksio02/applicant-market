package com.maboy.applicantmarket.applicant.dao.dto;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "applicant_privacy_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicantPrivacySettingsDto {

    @Id
    @Column(name = "applicant_id")
    private UUID applicantId;

    @Column(name = "visible_in_search", nullable = false)
    private Boolean visibleInSearch;

    @Column(name = "allow_invitations", nullable = false)
    private Boolean allowInvitations;

    @Column(name = "show_contacts_after_accept", nullable = false)
    private Boolean showContactsAfterAccept;

    @Column(name = "show_fsp_achievements", nullable = false)
    private Boolean showFspAchievements;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    void touch() {
        this.updatedAt = Instant.now();
        if (visibleInSearch == null) visibleInSearch = true;
        if (allowInvitations == null) allowInvitations = true;
        if (showContactsAfterAccept == null) showContactsAfterAccept = true;
        if (showFspAchievements == null) showFspAchievements = true;
    }
}