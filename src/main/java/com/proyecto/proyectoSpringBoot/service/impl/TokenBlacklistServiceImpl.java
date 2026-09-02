package com.proyecto.proyectoSpringBoot.service.impl;

import com.proyecto.proyectoSpringBoot.security.JwtUtil;
import com.proyecto.proyectoSpringBoot.service.interfaces.ITokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistServiceImpl implements ITokenBlacklistService {

    private static final String BLACKLIST_PREFIX = "token_blacklist:";
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtUtil jwtUtil;

    @Override
    public void blacklistToken(String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        try {
            long remainingMs = jwtUtil.getRemainingExpirationMs(token);
            if (remainingMs > 0) {
                String key = BLACKLIST_PREFIX + token;
                redisTemplate.opsForValue().set(key, "REVOKED", remainingMs, TimeUnit.MILLISECONDS);
                log.info("Token JWT revocado exitosamente en Redis con TTL de {} ms", remainingMs);
            }
        } catch (Exception e) {
            log.warn("No se pudo registrar token en la lista negra de Redis: {}", e.getMessage());
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        try {
            String key = BLACKLIST_PREFIX + token;
            Boolean exists = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.debug("No se pudo verificar lista negra en Redis: {}", e.getMessage());
            return false;
        }
    }
}
