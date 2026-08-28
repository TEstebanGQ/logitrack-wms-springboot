package com.proyecto.proyectoSpringBoot.model.entity;

import com.proyecto.proyectoSpringBoot.model.enums.EstadoEnvio;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "guias_despacho")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GuiaDespacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "numero_guia", nullable = false, unique = true, length = 60)
    private String numeroGuia;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private PedidoCliente pedido;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transportadora_id", nullable = false)
    private Transportadora transportadora;

    @NotNull
    @Column(name = "fecha_despacho", nullable = false)
    private LocalDateTime fechaDespacho;

    @Column(name = "fecha_entrega_estimada")
    private LocalDate fechaEntregaEstimada;

    @Column(name = "fecha_entrega_real")
    private LocalDateTime fechaEntregaReal;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_envio", nullable = false, length = 30)
    @Builder.Default
    private EstadoEnvio estadoEnvio = EstadoEnvio.EN_PREPARACION;

    @Column(name = "conductor_nombre", length = 100)
    private String conductorNombre;

    @Column(name = "placa_vehiculo", length = 20)
    private String placaVehiculo;

    @Column(name = "costo_flete", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal costoFlete = BigDecimal.ZERO;

    @Column(length = 500)
    private String observaciones;

    @PrePersist
    protected void onCreate() {
        if (fechaDespacho == null) fechaDespacho = LocalDateTime.now();
        if (estadoEnvio == null) estadoEnvio = EstadoEnvio.EN_PREPARACION;
        if (costoFlete == null) costoFlete = BigDecimal.ZERO;
    }
}
