package com.proyecto.proyectoSpringBoot.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InventarioBodegaResponse {
    private Long bodegaId;
    private String bodegaNombre;
    private Long productoId;
    private String productoNombre;
    private Integer stockActual;
}
