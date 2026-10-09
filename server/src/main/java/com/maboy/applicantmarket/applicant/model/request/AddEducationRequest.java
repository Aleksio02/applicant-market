package com.maboy.applicantmarket.applicant.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на добавление записи об образовании")
public class AddEducationRequest {

    @Schema(description = "Название учебного заведения",
            example = "МГУ им. М. В. Ломоносова")
    private String institution;

    @Schema(description = "Степень или квалификация",
            example = "Бакалавр")
    private String degree;

    @Schema(description = "Специальность",
            example = "Прикладная математика и информатика")
    private String field;

    @Schema(description = "Год начала обучения", example = "2015")
    private Short startYear;

    @Schema(description = "Год окончания обучения", example = "2019")
    private Short endYear;
}