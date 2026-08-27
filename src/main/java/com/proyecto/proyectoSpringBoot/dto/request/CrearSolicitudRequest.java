package com.proyecto.proyectoSpringBoot.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class CrearSolicitudRequest {
    @NotNull
    private Long bodegaOrigenId;
    
    @NotNull
    private Long bodegaDestinoId;
    
    @Size(max=500)
    private String observaciones;
    
    @NotEmpty
    @Valid
    private List<DetalleSolicitudRequest> detalles;
    
    @Data
    public static class DetalleSolicitudRequest {
        @NotNull
        private Long productoId;
        
        @NotNull
        @Min(1)
        private Integer cantidad;
    }
}
