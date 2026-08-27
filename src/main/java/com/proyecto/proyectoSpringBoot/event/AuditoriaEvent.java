package com.proyecto.proyectoSpringBoot.event;

import com.proyecto.proyectoSpringBoot.model.enums.TipoOperacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaEvent {
    private String entidad;
    private Long entidadId;
    private TipoOperacion tipoOperacion;
    private String emailUsuario;
    private String valoresAnteriores;
    private String valoresNuevos;
    private String descripcion;
}
