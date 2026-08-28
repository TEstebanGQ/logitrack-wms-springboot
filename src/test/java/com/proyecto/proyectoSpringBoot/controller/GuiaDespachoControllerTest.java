package com.proyecto.proyectoSpringBoot.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GuiaDespachoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("01. GET /api/guias-despacho debe retornar HTTP 200")
    void testListarGuias() throws Exception {
        mockMvc.perform(get("/api/guias-despacho"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("02. GET /api/transportadoras debe retornar HTTP 200")
    void testListarTransportadoras() throws Exception {
        mockMvc.perform(get("/api/transportadoras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
