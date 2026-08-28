package com.proyecto.proyectoSpringBoot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.proyectoSpringBoot.dto.request.CrearOrdenCompraRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrdenCompraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("01. GET /api/ordenes-compra debe retornar HTTP 200")
    void testListarOrdenes() throws Exception {
        mockMvc.perform(get("/api/ordenes-compra"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@logitrack.com", roles = "ADMIN")
    @DisplayName("02. POST /api/ordenes-compra con datos válidos debe retornar HTTP 201 Created")
    void testCrearOrdenCompraValida() throws Exception {
        CrearOrdenCompraRequest req = CrearOrdenCompraRequest.builder()
                .proveedorId(1L)
                .bodegaDestinoId(1L)
                .detalles(List.of(
                        CrearOrdenCompraRequest.DetalleOrdenRequest.builder()
                                .productoId(1L)
                                .cantidad(4)
                                .precioUnitario(new BigDecimal("4000000.00"))
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/ordenes-compra")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.codigoOrden").exists());
    }

    @Test
    @WithMockUser(username = "admin@logitrack.com", roles = "ADMIN")
    @DisplayName("03. POST /api/ordenes-compra sin detalles debe retornar HTTP 400 Bad Request")
    void testCrearOrdenCompraSinDetallesFalla() throws Exception {
        CrearOrdenCompraRequest req = CrearOrdenCompraRequest.builder()
                .proveedorId(1L)
                .bodegaDestinoId(1L)
                .detalles(List.of()) // Vacío @NotEmpty
                .build();

        mockMvc.perform(post("/api/ordenes-compra")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("04. GET /api/ordenes-compra/estado/PENDIENTE debe retornar HTTP 200")
    void testListarPorEstado() throws Exception {
        mockMvc.perform(get("/api/ordenes-compra/estado/PENDIENTE"))
                .andExpect(status().isOk());
    }
}
