package com.proyecto.proyectoSpringBoot.dto.response;

import com.proyecto.proyectoSpringBoot.model.enums.EstadoPicking;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TareaPickingResponse {
    private Long id;
    private String codigoTarea;
    private Long pedidoId;
    private String codigoPedido;
    private Long productoId;
    private String productoNombre;
    private Long ubicacionOrigenId;
    private String codigoUbicacion;
    private Long usuarioAsignadoId;
    private String usuarioAsignadoNombre;
    private Integer cantidadRequerida;
    private Integer cantidadRecogida;
    private EstadoPicking estado;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaCompletada;
    private String notas;
}
