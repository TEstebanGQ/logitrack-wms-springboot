package com.proyecto.proyectoSpringBoot.dto.response;

import com.proyecto.proyectoSpringBoot.model.enums.EstadoLote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoteResponse {
    private Long id;
    private String codigoLote;
    private Long productoId;
    private String productoNombre;
    private Long bodegaId;
    private String bodegaNombre;
    private Integer stockInicial;
    private Integer stockActual;
    private LocalDate fechaFabricacion;
    private LocalDate fechaVencimiento;
    private EstadoLote estado;
    private boolean proximoAVencer;
    private boolean vencido;
    private LocalDateTime createdAt;
}
