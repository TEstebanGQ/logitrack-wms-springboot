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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RbacPermissionsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "empleado@logitrack.com", roles = {"EMPLEADO"})
    @DisplayName("01. EMPLEADO no puede listar usuarios (403 Forbidden)")
    void testEmpleadoCannotListUsers() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@logitrack.com", roles = {"ADMIN"})
    @DisplayName("02. ADMIN puede listar usuarios (200 OK)")
    void testAdminCanListUsers() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "empleado@logitrack.com", roles = {"EMPLEADO"})
    @DisplayName("03. EMPLEADO no puede aprobar órdenes de compra (403 Forbidden)")
    void testEmpleadoCannotApprovePurchaseOrder() throws Exception {
        mockMvc.perform(put("/api/ordenes-compra/1/aprobar"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "jefe@logitrack.com", roles = {"JEFE_COMPRAS"})
    @DisplayName("04. JEFE_COMPRAS tiene permiso para aprobar órdenes (no 403)")
    void testJefeComprasCanAccessApprovePurchaseOrder() throws Exception {
        mockMvc.perform(put("/api/ordenes-compra/99999/aprobar"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == 403) {
                        throw new AssertionError("JEFE_COMPRAS no debe recibir 403 Forbidden en aprobar órdenes");
                    }
                });
    }

    @Test
    @WithMockUser(username = "empleado@logitrack.com", roles = {"EMPLEADO"})
    @DisplayName("05. EMPLEADO no puede registrar ajustes de inventario (403 Forbidden)")
    void testEmpleadoCannotCreateInventoryAdjustment() throws Exception {
        String json = """
                {
                    "bodegaId": 1,
                    "productoId": 1,
                    "tipoAjuste": "MERMA",
                    "cantidadNueva": 10,
                    "justificacion": "Ajuste de prueba"
                }
                """;

        mockMvc.perform(post("/api/ajustes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "supervisor@logitrack.com", roles = {"SUPERVISOR"})
    @DisplayName("06. SUPERVISOR tiene permiso para registrar ajustes de inventario (no 403)")
    void testSupervisorCanAccessCreateInventoryAdjustment() throws Exception {
        String json = """
                {
                    "bodegaId": 1,
                    "productoId": 1,
                    "tipoAjuste": "MERMA",
                    "cantidadNueva": 15,
                    "justificacion": "Ajuste mensual supervisor"
                }
                """;

        mockMvc.perform(post("/api/ajustes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == 403) {
                        throw new AssertionError("SUPERVISOR no debe recibir 403 Forbidden en ajustes");
                    }
                });
    }

    @Test
    @WithMockUser(username = "empleado@logitrack.com", roles = {"EMPLEADO"})
    @DisplayName("07. EMPLEADO no puede eliminar productos (403 Forbidden)")
    void testEmpleadoCannotDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "empleado@logitrack.com", roles = {"EMPLEADO"})
    @DisplayName("08. EMPLEADO no puede acceder al módulo de reportes (403 Forbidden)")
    void testEmpleadoCannotAccessReports() throws Exception {
        mockMvc.perform(get("/api/reportes/general"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "gerente@logitrack.com", roles = {"GERENTE_LOGISTICA"})
    @DisplayName("09. GERENTE_LOGISTICA puede acceder al módulo de reportes (200 OK)")
    void testGerenteCanAccessReports() throws Exception {
        mockMvc.perform(get("/api/reportes/general"))
                .andExpect(status().isOk());
    }
}
