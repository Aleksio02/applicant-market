package com.maboy.applicantmarket.applicant.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на обновление записи об образовании. Пустые поля не изменяются.")
public class UpdateEducationRequest {

    @Schema(description = "Название учебного заведения",
            example = "МГУ им. М. В. Ломоносова")
    private String institution;

    @Schema(description = "Степень или квалификация",
            example = "Магистр")
    private String degree;

    @Schema(description = "Специальность",
            example = "Прикладная математика и информатика")
    private String field;

    @Schema(description = "Год начала обучения", example = "2015")
    private Short startYear;

    @Schema(description = "Год окончания обучения", example = "2021")
    private Short endYear;
}