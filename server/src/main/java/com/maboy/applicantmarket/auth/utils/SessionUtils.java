package com.maboy.applicantmarket.auth.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.maboy.applicantmarket.commons.model.SessionPayload;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class SessionUtils {

    private static final long SESSION_TTL_SECONDS = 3600;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    public SessionUtils(@Qualifier("sessionRedisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public String createSession(UUID userId) {
        String sessionId = generateId();
        SessionPayload payload = new SessionPayload(userId);
        redisTemplate.opsForValue().set(sessionId, writeAsString(payload), SESSION_TTL_SECONDS, TimeUnit.SECONDS);
        return sessionId;
    }

    public SessionPayload getSession(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return null;
        }
        String rawJson = redisTemplate.opsForValue().get(sessionId);
        if (rawJson == null) {
            return null;
        }
        return readAsPayload(rawJson);
    }

    public void writeSessionCookie(HttpServletResponse response, String sessionId) {
        response.addHeader(
                HttpHeaders.SET_COOKIE,
                buildCookie(sessionId, Duration.ofSeconds(SESSION_TTL_SECONDS)).toString()
        );
    }

    private ResponseCookie buildCookie(String value, Duration maxAge) {
        return ResponseCookie
                .from("sessionId", value)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAge)
                .build();
    }

    public SessionPayload extendSession(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return null;
        }
        SessionPayload payload = getSession(sessionId);
        if (payload == null) {
            return null;
        }
        payload.setExpires(Instant.now().plusSeconds(SESSION_TTL_SECONDS));
        deleteSession(sessionId);
        redisTemplate.opsForValue().set(sessionId, writeAsString(payload), SESSION_TTL_SECONDS, TimeUnit.SECONDS);
        return payload;
    }

    public void deleteSession(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return;
        }
        redisTemplate.delete(sessionId);
    }

    private String generateId() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    private String writeAsString(SessionPayload payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize session payload", e);
        }
    }

    private SessionPayload readAsPayload(String rawJson) {
        if (rawJson == null) {
            return null;
        }
        try {
            return objectMapper.readValue(rawJson.replace("\u0000", ""), SessionPayload.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize session payload", e);
        }
    }
}