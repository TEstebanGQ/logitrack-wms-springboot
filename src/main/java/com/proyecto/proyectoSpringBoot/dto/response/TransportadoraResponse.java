package com.proyecto.proyectoSpringBoot.dto.response;

import com.proyecto.proyectoSpringBoot.model.enums.TipoServicioTransporte;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransportadoraResponse {
    private Long id;
    private String nombre;
    private String rucNit;
    private String telefono;
    private String email;
    private String contacto;
    private TipoServicioTransporte tipoServicio;
    private Boolean activo;
    private LocalDateTime createdAt;
}
