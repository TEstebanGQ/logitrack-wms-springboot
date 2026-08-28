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
class AlertaStockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("01. GET /api/alertas debe retornar HTTP 200")
    void testListarAlertas() throws Exception {
        mockMvc.perform(get("/api/alertas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("02. GET /api/alertas/pendientes debe retornar HTTP 200")
    void testListarAlertasPendientes() throws Exception {
        mockMvc.perform(get("/api/alertas/pendientes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("03. GET /api/alertas/producto/{id} debe retornar HTTP 200")
    void testListarAlertasPorProducto() throws Exception {
        mockMvc.perform(get("/api/alertas/producto/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@logitrack.com", roles = "ADMIN")
    @DisplayName("04. PUT /api/alertas/{id}/resolver con alerta inexistente debe retornar HTTP 404")
    void testResolverAlertaInexistente() throws Exception {
        mockMvc.perform(put("/api/alertas/99999/resolver"))
                .andExpect(status().isNotFound());
    }
}
