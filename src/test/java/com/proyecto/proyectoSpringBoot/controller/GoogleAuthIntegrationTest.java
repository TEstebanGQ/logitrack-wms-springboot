package com.proyecto.proyectoSpringBoot.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GoogleAuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("01. GET /api/config/public debe ser público y retornar googleClientId")
    void testPublicConfig() throws Exception {
        mockMvc.perform(get("/api/config/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.googleClientId").isNotEmpty());
    }

    @Test
    @DisplayName("02. POST /api/auth/google con token inválido debe responder HTTP 400 sin caerse")
    void testGoogleAuthInvalidToken() throws Exception {
        String json = """
                {
                    "credential": "invalid.jwt.token"
                }
                """;

        mockMvc.perform(post("/api/auth/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("03. GET /api/config/stats debe ser público y retornar estadísticas reales")
    void testHeroStatsPublic() throws Exception {
        mockMvc.perform(get("/api/config/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bodegasActivas").isNumber())
                .andExpect(jsonPath("$.totalMovimientos").isNumber())
                .andExpect(jsonPath("$.totalAuditorias").isNumber())
                .andExpect(jsonPath("$.auditoriaCoverage").value("100%"));
    }
}
