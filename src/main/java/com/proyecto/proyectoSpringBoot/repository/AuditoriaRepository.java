package com.proyecto.proyectoSpringBoot.repository;

import com.proyecto.proyectoSpringBoot.model.entity.Auditoria;
import com.proyecto.proyectoSpringBoot.model.enums.TipoOperacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long>, JpaSpecificationExecutor<Auditoria> {
    List<Auditoria> findByUsuarioId(Long usuarioId);
    List<Auditoria> findByTipoOperacion(TipoOperacion tipo);
    List<Auditoria> findByEntidad(String entidad);
    List<Auditoria> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);
}
