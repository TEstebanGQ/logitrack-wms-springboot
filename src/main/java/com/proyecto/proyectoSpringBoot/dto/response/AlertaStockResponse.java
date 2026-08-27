package com.proyecto.proyectoSpringBoot.dto.response;
import java.time.LocalDateTime;
import lombok.Data;
@Data
public class AlertaStockResponse {
    private Long id;
    private Long productoId;
    private String productoNombre;
    private Long bodegaId;
    private String bodegaNombre;
    private Integer stockActual;
    private Integer stockMinimo;
    private String estado;
    private LocalDateTime fechaGenerada;
    private LocalDateTime fechaResuelta;
    private String resueltaPorEmail;
}
