package com.maboy.applicantmarket.auth.service;

import com.maboy.applicantmarket.auth.model.request.Authorization;
import com.maboy.applicantmarket.auth.model.request.ConfirmEmailRequest;
import com.maboy.applicantmarket.auth.model.request.RegisterRequest;
import com.maboy.applicantmarket.auth.model.response.AuthResponse;
import com.maboy.applicantmarket.auth.model.response.RegisterResponse;
import com.maboy.applicantmarket.commons.model.SessionPayload;

public interface AuthService {
    AuthResponse login(Authorization request);
    RegisterResponse register(RegisterRequest request);
    AuthResponse confirmEmail(ConfirmEmailRequest request);
    SessionPayload validateSession(String sessionId);
}