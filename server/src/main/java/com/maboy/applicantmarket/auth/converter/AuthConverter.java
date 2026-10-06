package com.maboy.applicantmarket.auth.converter;

import com.maboy.applicantmarket.auth.model.request.RegisterRequest;
import com.maboy.applicantmarket.commons.dao.dto.UserDto;
import com.maboy.applicantmarket.commons.model.UserStatus;

public class AuthConverter {
    public void toDto(RegisterRequest source, UserDto destination) {
        destination.setEmail(source.getEmail());
        destination.setUsername(source.getLogin());
        destination.setPassword(source.getPassword());
        destination.setRole(source.getRole());
        destination.setStatus(UserStatus.PENDING_EMAIL);
    }
}