package com.proyecto.proyectoSpringBoot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionBodegaResponse {
    private Long id;
    private Long bodegaId;
    private String bodegaNombre;
    private String codigoUbicacion;
    private String pasillo;
    private String estante;
    private String nivel;
    private Integer capacidadMax;
    private String descripcion;
    private boolean activo;
    private LocalDateTime createdAt;
}
