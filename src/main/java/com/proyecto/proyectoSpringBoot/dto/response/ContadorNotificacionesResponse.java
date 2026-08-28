package com.proyecto.proyectoSpringBoot.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContadorNotificacionesResponse {
    private Long count;

    public Long getNoLeidas() {
        return count;
    }
}
