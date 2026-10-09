package com.maboy.applicantmarket.applicant.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на обновление профиля соискателя. Пустые поля не изменяются.")
public class UpdateApplicantProfileRequest {

    @Schema(description = "Имя", example = "Иван")
    private String firstName;

    @Schema(description = "Фамилия", example = "Иванов")
    private String lastName;

    @Schema(description = "Отчество", example = "Иванович")
    private String middleName;

    @Schema(description = "Контактный телефон", example = "+7 999 123-45-67")
    private String phone;

    @Schema(description = "Город", example = "Москва")
    private String city;

    @Schema(description = "Страна", example = "Россия")
    private String country;

    @Schema(description = "О себе — краткое описание опыта и интересов",
            example = "Backend-разработчик на Java, 5 лет опыта")
    private String about;

    @Schema(description = "Опыт работы в годах", example = "5")
    private Short experienceYears;
}