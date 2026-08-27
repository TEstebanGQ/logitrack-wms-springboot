package com.proyecto.proyectoSpringBoot.repository;

import com.proyecto.proyectoSpringBoot.model.entity.SolicitudTransferencia;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SolicitudTransferenciaRepository extends JpaRepository<SolicitudTransferencia, Long>, JpaSpecificationExecutor<SolicitudTransferencia> {
    List<SolicitudTransferencia> findBySolicitanteId(Long solicitanteId);
    List<SolicitudTransferencia> findByEstado(EstadoSolicitud estado);
    
    @Query("SELECT COALESCE(SUM(s.totalUnidades), 0) FROM SolicitudTransferencia s WHERE s.solicitante.id = :usuarioId AND s.estado IN (com.proyecto.proyectoSpringBoot.model.enums.EstadoSolicitud.APROBADA_AUTOMATICA, com.proyecto.proyectoSpringBoot.model.enums.EstadoSolicitud.EJECUTADA) AND s.fechaSolicitud >= :inicioDia")
    Long sumUnidadesHoy(@Param("usuarioId") Long usuarioId, @Param("inicioDia") LocalDateTime inicioDia);
}
