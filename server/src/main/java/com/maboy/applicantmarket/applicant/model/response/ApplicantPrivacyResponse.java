package com.maboy.applicantmarket.applicant.model.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantPrivacyResponse {
    private boolean visibleInSearch;
    private boolean allowInvitations;
    private boolean showContactsAfterAccept;
    private boolean showFspAchievements;
}