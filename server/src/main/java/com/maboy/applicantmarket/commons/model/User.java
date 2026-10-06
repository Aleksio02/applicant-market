package com.maboy.applicantmarket.commons.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class User {
    private UUID id;
    private String username;
    private String email;
    private Role role;
    private UserStatus status;
}