package com.proyecto.proyectoSpringBoot.repository;

import com.proyecto.proyectoSpringBoot.model.entity.SolicitudDetalleTransferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudDetalleTransferenciaRepository extends JpaRepository<SolicitudDetalleTransferencia, Long> {
}
