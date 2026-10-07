package com.maboy.applicantmarket.vacancy.model.request;

import com.maboy.applicantmarket.commons.model.enums.WorkFormat;
import com.maboy.applicantmarket.vacancy.model.enums.VacancyStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GetVacancyListRequest {
    private Integer page;
    private Integer pageSize;
    private UUID specializationId;
    private UUID gradeId;
    private WorkFormat format;
    private Long salaryFrom;
    private String location;
    private VacancyStatus status;
}