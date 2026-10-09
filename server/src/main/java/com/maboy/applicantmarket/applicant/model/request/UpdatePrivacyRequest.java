package com.maboy.applicantmarket.applicant.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на обновление настроек приватности. Пустые поля не изменяются.")
public class UpdatePrivacyRequest {

    @Schema(description = "Показывать профиль работодателям в поиске",
            example = "true")
    private Boolean visibleInSearch;

    @Schema(description = "Разрешить работодателям отправлять приглашения",
            example = "true")
    private Boolean allowInvitations;

    @Schema(description = "Раскрывать контакты работодателю после принятия приглашения",
            example = "true")
    private Boolean showContactsAfterAccept;

    @Schema(description = "Показывать работодателю достижения ФСП",
            example = "true")
    private Boolean showFspAchievements;
}