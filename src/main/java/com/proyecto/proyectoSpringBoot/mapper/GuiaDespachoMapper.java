package com.proyecto.proyectoSpringBoot.mapper;

import com.proyecto.proyectoSpringBoot.dto.response.GuiaDespachoResponse;
import com.proyecto.proyectoSpringBoot.model.entity.GuiaDespacho;
import org.springframework.stereotype.Component;

@Component
public class GuiaDespachoMapper {

    public GuiaDespachoResponse toResponse(GuiaDespacho g) {
        if (g == null) return null;
        return GuiaDespachoResponse.builder()
                .id(g.getId())
                .numeroGuia(g.getNumeroGuia())
                .pedidoId(g.getPedido() != null ? g.getPedido().getId() : null)
                .codigoPedido(g.getPedido() != null ? g.getPedido().getCodigoPedido() : null)
                .clienteNombre(g.getPedido() != null && g.getPedido().getCliente() != null ? g.getPedido().getCliente().getNombre() : null)
                .transportadoraId(g.getTransportadora() != null ? g.getTransportadora().getId() : null)
                .transportadoraNombre(g.getTransportadora() != null ? g.getTransportadora().getNombre() : null)
                .fechaDespacho(g.getFechaDespacho())
                .fechaEntregaEstimada(g.getFechaEntregaEstimada())
                .fechaEntregaReal(g.getFechaEntregaReal())
                .estadoEnvio(g.getEstadoEnvio())
                .conductorNombre(g.getConductorNombre())
                .placaVehiculo(g.getPlacaVehiculo())
                .costoFlete(g.getCostoFlete())
                .observaciones(g.getObservaciones())
                .build();
    }
}
