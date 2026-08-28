package com.proyecto.proyectoSpringBoot.repository;

import com.proyecto.proyectoSpringBoot.model.entity.GuiaDespacho;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuiaDespachoRepository extends JpaRepository<GuiaDespacho, Long> {
    Optional<GuiaDespacho> findByNumeroGuia(String numeroGuia);
    Optional<GuiaDespacho> findByPedidoId(Long pedidoId);
    List<GuiaDespacho> findByTransportadoraId(Long transportadoraId);
    List<GuiaDespacho> findByEstadoEnvio(EstadoEnvio estadoEnvio);
    boolean existsByNumeroGuia(String numeroGuia);
}
