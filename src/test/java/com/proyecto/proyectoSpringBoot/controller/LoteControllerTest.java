package com.proyecto.proyectoSpringBoot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.proyectoSpringBoot.dto.request.CrearLoteRequest;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoLote;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("01. GET /api/lotes debe retornar HTTP 200")
    void testListarLotes() throws Exception {
        mockMvc.perform(get("/api/lotes"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@logitrack.com", roles = "ADMIN")
    @DisplayName("02. POST /api/lotes con datos válidos debe retornar HTTP 201 Created")
    void testCrearLoteValido() throws Exception {
        CrearLoteRequest req = CrearLoteRequest.builder()
                .codigoLote("LOT-MOCK-001")
                .productoId(1L)
                .bodegaId(1L)
                .stockInicial(35)
                .fechaFabricacion(LocalDate.now().minusDays(5))
                .fechaVencimiento(LocalDate.now().plusMonths(8))
                .estado(EstadoLote.DISPONIBLE)
                .build();

        mockMvc.perform(post("/api/lotes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.codigoLote").value("LOT-MOCK-001"));
    }

    @Test
    @WithMockUser(username = "admin@logitrack.com", roles = "ADMIN")
    @DisplayName("03. POST /api/lotes con código vacío debe retornar HTTP 400 Bad Request")
    void testCrearLoteInvalido() throws Exception {
        CrearLoteRequest req = CrearLoteRequest.builder()
                .codigoLote("") // Inválido @NotBlank
                .productoId(1L)
                .bodegaId(1L)
                .stockInicial(10)
                .build();

        mockMvc.perform(post("/api/lotes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("04. GET /api/lotes/proximos-vencer debe retornar HTTP 200")
    void testListarProximosAVencer() throws Exception {
        mockMvc.perform(get("/api/lotes/proximos-vencer?dias=60"))
                .andExpect(status().isOk());
    }
}
