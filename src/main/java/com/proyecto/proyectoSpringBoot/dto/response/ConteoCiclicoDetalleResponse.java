package com.proyecto.proyectoSpringBoot.dto.response;

import com.proyecto.proyectoSpringBoot.model.enums.EstadoLineaConteo;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConteoCiclicoDetalleResponse {
    private Long id;
    private Long productoId;
    private String productoNombre;
    private Long ubicacionId;
    private String codigoUbicacion;
    private Integer stockSistema;
    private Integer stockFisico;
    private Integer diferencia;
    private EstadoLineaConteo estadoLinea;
    private String notas;
}
