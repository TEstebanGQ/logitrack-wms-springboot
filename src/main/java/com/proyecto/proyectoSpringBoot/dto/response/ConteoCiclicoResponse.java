package com.proyecto.proyectoSpringBoot.dto.response;

import com.proyecto.proyectoSpringBoot.model.enums.EstadoConteo;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConteoCiclicoResponse {
    private Long id;
    private String codigoConteo;
    private Long bodegaId;
    private String bodegaNombre;
    private Long zonaId;
    private String zonaNombre;
    private LocalDate fechaProgramada;
    private LocalDateTime fechaEjecucion;
    private EstadoConteo estado;
    private String supervisorNombre;
    private String observaciones;
    private List<ConteoCiclicoDetalleResponse> detalles;
}
