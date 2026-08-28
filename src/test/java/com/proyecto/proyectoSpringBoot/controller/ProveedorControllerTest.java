package com.proyecto.proyectoSpringBoot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.proyectoSpringBoot.dto.request.CrearProveedorRequest;
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
class ProveedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("01. GET /api/proveedores debe retornar HTTP 200 y array JSON")
    void testListarProveedores() throws Exception {
        mockMvc.perform(get("/api/proveedores")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("02. POST /api/proveedores con datos válidos debe retornar HTTP 201 Created")
    void testCrearProveedorValido() throws Exception {
        CrearProveedorRequest req = new CrearProveedorRequest();
        req.setNombre("Distribuciones Globales S.A.");
        req.setRuc("900777555-8");
        req.setTelefono("3154443322");
        req.setEmail("info@globales.co");
        req.setDireccion("Avenida Las Américas # 45-67");

        mockMvc.perform(post("/api/proveedores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre").value("Distribuciones Globales S.A."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("03. POST /api/proveedores con nombre vacío debe retornar HTTP 400 Bad Request")
    void testCrearProveedorInvalido() throws Exception {
        CrearProveedorRequest req = new CrearProveedorRequest();
        req.setNombre(""); // Inválido @NotBlank

        mockMvc.perform(post("/api/proveedores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("04. GET /api/proveedores/{id}/movimientos debe retornar HTTP 200")
    void testListarMovimientosProveedor() throws Exception {
        mockMvc.perform(get("/api/proveedores/1/movimientos"))
                .andExpect(status().isOk());
    }
}
