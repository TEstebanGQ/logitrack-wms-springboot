package com.proyecto.proyectoSpringBoot.model.entity;

import com.proyecto.proyectoSpringBoot.model.enums.EstadoConteo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conteos_ciclicos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConteoCiclico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "codigo_conteo", nullable = false, unique = true, length = 50)
    private String codigoConteo;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bodega_id", nullable = false)
    private Bodega bodega;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zona_id")
    private ZonaBodega zona;

    @NotNull
    @Column(name = "fecha_programada", nullable = false)
    private LocalDate fechaProgramada;

    @Column(name = "fecha_ejecucion")
    private LocalDateTime fechaEjecucion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private EstadoConteo estado = EstadoConteo.PROGRAMADO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id", nullable = false)
    private Usuario supervisor;

    @Column(length = 500)
    private String observaciones;

    @OneToMany(mappedBy = "conteo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ConteoCiclicoDetalle> detalles = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (fechaProgramada == null) fechaProgramada = LocalDate.now();
        if (estado == null) estado = EstadoConteo.PROGRAMADO;
    }
}
