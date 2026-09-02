package com.proyecto.proyectoSpringBoot.service.interfaces;

public interface ITokenBlacklistService {

    /**
     * Registra un token JWT en la lista negra en Redis durante su tiempo restante de vida.
     * @param token Token JWT recibido
     */
    void blacklistToken(String token);

    /**
     * Verifica si un token JWT está en la lista negra de Redis.
     * @param token Token JWT a consultar
     * @return true si el token fue revocado, false si es válido
     */
    boolean isTokenBlacklisted(String token);
}
