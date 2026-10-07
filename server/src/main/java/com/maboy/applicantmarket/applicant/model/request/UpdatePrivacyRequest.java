package com.maboy.applicantmarket.applicant.model.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePrivacyRequest {
    private Boolean visibleInSearch;
    private Boolean allowInvitations;
    private Boolean showContactsAfterAccept;
    private Boolean showFspAchievements;
}