package com.proyecto.proyectoSpringBoot.dto.response;

import com.proyecto.proyectoSpringBoot.model.enums.TipoAjuste;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AjusteResponse {
    private Long id;
    private Long bodegaId;
    private String bodegaNombre;
    private Long productoId;
    private String productoNombre;
    private TipoAjuste tipoAjuste;
    private Integer cantidadAnterior;
    private Integer cantidadNueva;
    private Integer diferencia;
    private String justificacion;
    private String usuarioNombre;
    private LocalDateTime fecha;
}
