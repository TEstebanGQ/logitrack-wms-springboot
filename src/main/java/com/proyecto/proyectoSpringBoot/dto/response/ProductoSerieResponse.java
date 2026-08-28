package com.proyecto.proyectoSpringBoot.dto.response;

import com.proyecto.proyectoSpringBoot.model.enums.EstadoSerie;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductoSerieResponse {
    private Long id;
    private String numeroSerie;
    private Long productoId;
    private String productoNombre;
    private Long bodegaId;
    private String bodegaNombre;
    private Long ubicacionId;
    private String codigoUbicacion;
    private EstadoSerie estado;
    private LocalDateTime fechaIngreso;
    private LocalDateTime fechaDespacho;
    private String observaciones;
}
