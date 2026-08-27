package com.proyecto.proyectoSpringBoot.dto.response;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
@Data
public class SolicitudTransferenciaResponse {
    private Long id;
    private String bodegaOrigen;
    private String bodegaDestino;
    private String solicitanteNombre;
    private String aprobadorNombre;
    private String estado;
    private String observaciones;
    private String motivoRechazo;
    private Integer totalUnidades;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaResolucion;
    private LocalDateTime fechaExpiracion;
    private List<DetalleSolicitudResponse> detalles;
    
    @Data
    public static class DetalleSolicitudResponse {
        private Long productoId;
        private String productoNombre;
        private Integer cantidad;
    }
}
