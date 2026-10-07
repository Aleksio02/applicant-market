package com.maboy.applicantmarket.commons.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Skill {
    private UUID id;
    private String code;
    private String name;
    private String category;
}