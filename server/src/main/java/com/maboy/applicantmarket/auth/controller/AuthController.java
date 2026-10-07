package com.maboy.applicantmarket.auth.controller;

import com.maboy.applicantmarket.auth.model.request.AuthorizationRequest;
import com.maboy.applicantmarket.auth.model.request.ConfirmEmailRequest;
import com.maboy.applicantmarket.auth.model.request.RegisterRequest;
import com.maboy.applicantmarket.auth.model.response.AuthResponse;
import com.maboy.applicantmarket.auth.model.response.RegisterResponse;
import com.maboy.applicantmarket.auth.service.AuthService;
import com.maboy.applicantmarket.auth.utils.SessionUtils;
import com.maboy.applicantmarket.commons.model.SessionPayload;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final SessionUtils sessionUtils;

    public AuthController(AuthService authService, SessionUtils sessionUtils) {
        this.authService = authService;
        this.sessionUtils = sessionUtils;
    }

    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/confirm-email")
    public AuthResponse confirmEmail(@Valid @RequestBody ConfirmEmailRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.confirmEmail(request);
        sessionUtils.writeSessionCookie(response, authResponse.getToken());
        return authResponse;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthorizationRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        sessionUtils.writeSessionCookie(response, authResponse.getToken());
        return authResponse;
    }

    @GetMapping("/validateSession")
    public SessionPayload validateSession(@CookieValue(name = "sessionId", required = false) String sessionId,
                                          HttpServletResponse response) {
        SessionPayload payload = authService.validateSession(sessionId);
        sessionUtils.writeSessionCookie(response, sessionId);
        return payload;
    }
}