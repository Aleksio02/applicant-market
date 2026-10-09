package com.maboy.applicantmarket.auth.controller;

import com.maboy.applicantmarket.auth.model.request.AuthorizationRequest;
import com.maboy.applicantmarket.auth.model.request.ConfirmEmailRequest;
import com.maboy.applicantmarket.auth.model.request.RegisterRequest;
import com.maboy.applicantmarket.auth.model.response.AuthResponse;
import com.maboy.applicantmarket.auth.model.response.RegisterResponse;
import com.maboy.applicantmarket.auth.service.AuthService;
import com.maboy.applicantmarket.auth.utils.SessionUtils;
import com.maboy.applicantmarket.commons.model.SessionPayload;
import com.maboy.applicantmarket.commons.model.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "Регистрация, вход, подтверждение email, проверка сессии")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final SessionUtils sessionUtils;

    public AuthController(AuthService authService, SessionUtils sessionUtils) {
        this.authService = authService;
        this.sessionUtils = sessionUtils;
    }

    @Operation(summary = "Регистрация")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или не принят обязательный DATA_PROCESSING",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @Operation(summary = "Подтверждение email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email подтверждён, выдана сессия"),
            @ApiResponse(responseCode = "400", description = "Неверный или просроченный код",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/confirm-email")
    public AuthResponse confirmEmail(@Valid @RequestBody ConfirmEmailRequest request,
                                     HttpServletResponse response) {
        AuthResponse authResponse = authService.confirmEmail(request);
        sessionUtils.writeSessionCookie(response, authResponse.getToken());
        return authResponse;
    }

    @Operation(summary = "Вход")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешный вход"),
            @ApiResponse(responseCode = "401", description = "Пользователь не найден, неверный пароль или email не подтверждён",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthorizationRequest request,
                              HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        sessionUtils.writeSessionCookie(response, authResponse.getToken());
        return authResponse;
    }

    @Operation(summary = "Проверка сессии",
            description = "Проверяет cookie sessionId, продлевает сессию и возвращает текущего пользователя.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сессия валидна"),
            @ApiResponse(responseCode = "401", description = "Сессия не найдена или истекла",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @GetMapping("/validateSession")
    public SessionPayload validateSession(
            @Parameter(hidden = true)
            @CookieValue(name = "sessionId", required = false) String sessionId,
            HttpServletResponse response) {
        SessionPayload payload = authService.validateSession(sessionId);
        sessionUtils.writeSessionCookie(response, sessionId);
        return payload;
    }
}