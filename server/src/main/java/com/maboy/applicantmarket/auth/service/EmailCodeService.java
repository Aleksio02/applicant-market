package com.maboy.applicantmarket.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class EmailCodeService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final long TTL_MINUTES = 15;

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.mail.username}")
    private String sourceEmail;

    public EmailCodeService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateAndStore(UUID userId) {
        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        redisTemplate.opsForValue().set(key(userId), code, TTL_MINUTES, TimeUnit.MINUTES);
        return code;
    }

    public boolean verify(UUID userId, String code) {
        String stored = redisTemplate.opsForValue().get(key(userId));
        if (stored == null || !stored.equals(code)) {
            return false;
        }
        redisTemplate.delete(key(userId));
        return true;
    }

    private String key(UUID userId) {
        return "email:confirm:" + userId;
    }
}