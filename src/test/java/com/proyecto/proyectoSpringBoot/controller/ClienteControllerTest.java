package com.proyecto.proyectoSpringBoot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.proyectoSpringBoot.dto.request.CrearClienteRequest;
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
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("01. GET /api/clientes debe retornar HTTP 200 y array JSON")
    void testListarClientes() throws Exception {
        mockMvc.perform(get("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("02. POST /api/clientes con datos válidos debe retornar HTTP 201 Created")
    void testCrearClienteValido() throws Exception {
        CrearClienteRequest req = new CrearClienteRequest();
        req.setNombre("Cliente MockMvc S.A.");
        req.setRuc("900888111-2");
        req.setTelefono("3101234567");
        req.setEmail("mock@cliente.com");
        req.setDireccion("Calle Mock 123");

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre").value("Cliente MockMvc S.A."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("03. POST /api/clientes con nombre vacío debe retornar HTTP 400 Bad Request")
    void testCrearClienteInvalido() throws Exception {
        CrearClienteRequest req = new CrearClienteRequest();
        req.setNombre(""); // Invalido @NotBlank

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("04. GET /api/clientes/{id}/movimientos debe retornar HTTP 200")
    void testListarMovimientosCliente() throws Exception {
        mockMvc.perform(get("/api/clientes/1/movimientos"))
                .andExpect(status().isOk());
    }
}
