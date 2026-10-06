package com.maboy.applicantmarket.commons.converter;

import com.maboy.applicantmarket.commons.dao.dto.UserDto;
import com.maboy.applicantmarket.commons.model.User;

public class UserConverter {
    public void fromDto(UserDto source, User destination) {
        destination.setId(source.getId());
        destination.setUsername(source.getUsername());
        destination.setEmail(source.getEmail());
        destination.setRole(source.getRole());
        destination.setStatus(source.getStatus());
    }
}