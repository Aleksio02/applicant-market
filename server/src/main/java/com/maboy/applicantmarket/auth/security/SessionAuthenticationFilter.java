package com.maboy.applicantmarket.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maboy.applicantmarket.auth.service.AuthService;
import com.maboy.applicantmarket.commons.model.SessionPayload;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class SessionAuthenticationFilter extends OncePerRequestFilter {

    private final AuthService authService;
    private final ObjectMapper objectMapper;

    /** Пути, которые не требуют аутентификации вообще. */
    private static final List<String> PUBLIC_PATH_PREFIXES = List.of(
        "/api/auth/register",
        "/api/auth/login",
        "/api/auth/confirm-email",
        "/api/auth/validateSession",
        "/actuator/health",
        "/v3/api-docs",
        "/swagger-ui",
        "/error"
    );

    /**
     * Требования по ролям.
     * role = null — любая аутентифицированная роль.
     * Порядок важен: первое совпадение по prefix выигрывает.
     * Более специфичные пути — выше в списке.
     */
    private static final List<PathRole> PATH_ROLES = List.of(
        new PathRole("/api/applicant/",      "APPLICANT"),
        new PathRole("/api/assessment/",     "APPLICANT"),
        new PathRole("/api/matching/",       "EMPLOYER"),
        new PathRole("/api/employer/",       "EMPLOYER"),
        new PathRole("/api/vacancy/",        null),
        new PathRole("/api/interaction/",    null),   // и APPLICANT, и EMPLOYER
        new PathRole("/api/assignment/",    null)     // и APPLICANT, и EMPLOYER
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // 1. CORS preflight и публичные пути пропускаем без проверок
        if ("OPTIONS".equalsIgnoreCase(request.getMethod()) || isPublic(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Извлекаем токен из заголовка или cookie
        String token = extractSession(request);
        if (token == null) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED,
                "Authorization header or session cookie is required");
            return;
        }

        // 3. Валидируем токен и получаем principal
        SessionPayload principal;
        try {
            principal = authService.validateSession(token);
        } catch (RuntimeException e) {
            log.debug("Invalid token on {}: {}", path, e.getMessage());
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED,
                "Invalid or expired token");
            return;
        }

        // 4. Проверяем роль по пути
        String requiredRole = findRequiredRole(path);
        if (requiredRole != null && !requiredRole.equals(principal.getCurrentUser().getRole().name())) {
            log.debug("Forbidden: user role={} path={} required={}",
                principal.getCurrentUser().getRole().name(), path, requiredRole);
            writeError(response, HttpServletResponse.SC_FORBIDDEN,
                "Role " + requiredRole + " is required for this endpoint");
            return;
        }

        // 5. Кладём аутентификацию в SecurityContext
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + principal.getCurrentUser().getRole()))
            );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Чистим контекст, чтобы не утекло между запросами
            SecurityContextHolder.clearContext();
        }
    }

    // ============================================================
    // helpers
    // ============================================================

    private boolean isPublic(String path) {
        return PUBLIC_PATH_PREFIXES.stream().anyMatch(path::startsWith);
    }

    private String extractSession(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
            .filter(c -> "sessionId".equals(c.getName()))
            .findFirst()
            .map(Cookie::getValue)
            .orElse(null);
    }

    private String findRequiredRole(String path) {
        for (PathRole pr : PATH_ROLES) {
            if (path.startsWith(pr.prefix())) {
                return pr.role();
            }
        }
        return null;
    }

    private void writeError(HttpServletResponse response, int status, String message)
        throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), Map.of("message", message));
    }

    private record PathRole(String prefix, String role) {}
}