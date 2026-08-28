package com.proyecto.proyectoSpringBoot.repository;

import com.proyecto.proyectoSpringBoot.model.entity.UbicacionBodega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UbicacionBodegaRepository extends JpaRepository<UbicacionBodega, Long> {
    List<UbicacionBodega> findByBodegaId(Long bodegaId);
    List<UbicacionBodega> findByBodegaIdAndActivoTrue(Long bodegaId);
    boolean existsByBodegaIdAndCodigoUbicacion(Long bodegaId, String codigoUbicacion);
    List<UbicacionBodega> findByBodegaIdAndPasillo(Long bodegaId, String pasillo);
}
