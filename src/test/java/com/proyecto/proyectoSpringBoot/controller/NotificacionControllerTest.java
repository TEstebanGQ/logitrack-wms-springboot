package com.proyecto.proyectoSpringBoot.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class NotificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "admin@logitrack.com", roles = "ADMIN")
    @DisplayName("01. GET /api/notificaciones debe retornar HTTP 200")
    void testListarMisNotificaciones() throws Exception {
        mockMvc.perform(get("/api/notificaciones")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@logitrack.com", roles = "ADMIN")
    @DisplayName("02. GET /api/notificaciones/no-leidas/contador debe retornar HTTP 200 y contador")
    void testContadorNoLeidas() throws Exception {
        mockMvc.perform(get("/api/notificaciones/no-leidas/contador")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.noLeidas").isNumber());
    }

    @Test
    @WithMockUser(username = "admin@logitrack.com", roles = "ADMIN")
    @DisplayName("03. PUT /api/notificaciones/leer-todas debe retornar HTTP 204 No Content")
    void testMarcarTodasLeidas() throws Exception {
        mockMvc.perform(put("/api/notificaciones/leer-todas"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin@logitrack.com", roles = "ADMIN")
    @DisplayName("04. PUT /api/notificaciones/{id}/leer con ID inexistente debe retornar HTTP 404")
    void testMarcarLeidaInexistente() throws Exception {
        mockMvc.perform(put("/api/notificaciones/99999/leer"))
                .andExpect(status().isNotFound());
    }
}
