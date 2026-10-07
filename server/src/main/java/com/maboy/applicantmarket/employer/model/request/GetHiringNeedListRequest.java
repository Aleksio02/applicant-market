package com.maboy.applicantmarket.employer.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetHiringNeedListRequest {
    private Integer page;
    private Integer pageSize;
    private Boolean active;
}