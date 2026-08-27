package com.proyecto.proyectoSpringBoot.dto.response;
import java.time.LocalDateTime;
import lombok.Data;
@Data
public class NotificacionResponse {
    private Long id;
    private String titulo;
    private String mensaje;
    private String tipo;
    private boolean leida;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaLectura;
    private String urlAccion;
}
