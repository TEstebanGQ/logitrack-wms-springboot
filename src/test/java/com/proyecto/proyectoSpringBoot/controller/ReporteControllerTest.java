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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("01. GET /api/reportes/general debe retornar HTTP 200")
    void testReporteGeneral() throws Exception {
        mockMvc.perform(get("/api/reportes/general"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockPorBodega").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("02. GET /api/reportes/movimientos con filtros debe retornar HTTP 200")
    void testReporteMovimientosFiltrados() throws Exception {
        mockMvc.perform(get("/api/reportes/movimientos")
                .param("bodega", "1")
                .param("tipoMovimiento", "ENTRADA")
                .param("fechaInicio", "2026-01-01")
                .param("fechaFin", "2026-12-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("03. GET /api/reportes/auditoria con filtros debe retornar HTTP 200")
    void testReporteAuditoriaFiltrada() throws Exception {
        mockMvc.perform(get("/api/reportes/auditoria")
                .param("producto", "1")
                .param("campoModificado", "stock"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("04. GET /api/reportes/clasificacion-abc debe retornar HTTP 200")
    void testReporteClasificacionAbc() throws Exception {
        mockMvc.perform(get("/api/reportes/clasificacion-abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemsA").isArray());
    }
}
