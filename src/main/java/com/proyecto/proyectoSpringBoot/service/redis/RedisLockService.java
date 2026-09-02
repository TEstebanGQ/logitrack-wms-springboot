package com.proyecto.proyectoSpringBoot.service.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisLockService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Intenta adquirir un bloqueo distribuido con reintentos y tiempo de espera.
     *
     * @param lockKey       Clave del recurso a bloquear
     * @param acquireTimeoutMs Tiempo máximo para esperar la adquisición del lock (milisegundos)
     * @param lockTtlMs        Tiempo de vida del bloqueo (milisegundos) antes de auto-liberarse
     * @return Identificador único del lock si fue adquirido, o null si expiró el tiempo de espera
     */
    public String acquireLock(String lockKey, long acquireTimeoutMs, long lockTtlMs) {
        String lockValue = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        try {
            while ((System.currentTimeMillis() - startTime) < acquireTimeoutMs) {
                Boolean success = redisTemplate.opsForValue()
                        .setIfAbsent(lockKey, lockValue, Duration.ofMillis(lockTtlMs));

                if (Boolean.TRUE.equals(success)) {
                    return lockValue;
                }

                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Interrupción mientras se intentaba adquirir el lock: {}", lockKey);
        } catch (Exception e) {
            log.debug("Redis lock no disponible ({}), continuando con transacción de BD", e.getMessage());
            return "FALLBACK_LOCK";
        }

        return null;
    }

    /**
     * Libera el bloqueo de forma segura si el valor coincide.
     *
     * @param lockKey   Clave del recurso
     * @param lockValue Valor devuelto al adquirir el lock
     */
    public void releaseLock(String lockKey, String lockValue) {
        if (lockValue == null || "FALLBACK_LOCK".equals(lockValue)) {
            return;
        }
        try {
            Object currentValue = redisTemplate.opsForValue().get(lockKey);
            if (lockValue.equals(currentValue)) {
                redisTemplate.delete(lockKey);
            }
        } catch (Exception e) {
            log.warn("No se pudo liberar el lock de Redis {}: {}", lockKey, e.getMessage());
        }
    }
}
