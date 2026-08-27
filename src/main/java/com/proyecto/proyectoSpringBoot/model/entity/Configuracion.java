package com.proyecto.proyectoSpringBoot.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="configuracion")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Configuracion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 100, nullable = false)
    private String clave;

    @Column(length = 500, nullable = false)
    private String valor;

    @Column(length = 500)
    private String descripcion;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
