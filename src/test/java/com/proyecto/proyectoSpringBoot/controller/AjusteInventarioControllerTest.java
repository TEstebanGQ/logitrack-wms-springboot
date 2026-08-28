package com.proyecto.proyectoSpringBoot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.proyectoSpringBoot.dto.request.CrearAjusteRequest;
import com.proyecto.proyectoSpringBoot.model.enums.TipoAjuste;
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
class AjusteInventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("01. GET /api/ajustes debe retornar HTTP 200")
    void testListarAjustes() throws Exception {
        mockMvc.perform(get("/api/ajustes"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin@logitrack.com", roles = "ADMIN")
    @DisplayName("02. POST /api/ajustes con datos válidos debe retornar HTTP 201 Created")
    void testRegistrarAjusteValido() throws Exception {
        CrearAjusteRequest req = CrearAjusteRequest.builder()
                .bodegaId(1L)
                .productoId(1L)
                .tipoAjuste(TipoAjuste.MERMA)
                .cantidadNueva(18)
                .justificacion("Prueba MockMvc merma")
                .build();

        mockMvc.perform(post("/api/ajustes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.tipoAjuste").value("MERMA"));
    }

    @Test
    @WithMockUser(username = "admin@logitrack.com", roles = "ADMIN")
    @DisplayName("03. POST /api/ajustes con justificación vacía debe retornar HTTP 400 Bad Request")
    void testRegistrarAjusteInvalido() throws Exception {
        CrearAjusteRequest req = CrearAjusteRequest.builder()
                .bodegaId(1L)
                .productoId(1L)
                .tipoAjuste(TipoAjuste.MERMA)
                .cantidadNueva(18)
                .justificacion("") // Inválido @NotBlank
                .build();

        mockMvc.perform(post("/api/ajustes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("04. GET /api/ajustes/tipo/MERMA debe retornar HTTP 200")
    void testListarPorTipo() throws Exception {
        mockMvc.perform(get("/api/ajustes/tipo/MERMA"))
                .andExpect(status().isOk());
    }
}
