package com.proyecto.proyectoSpringBoot.repository;

import com.proyecto.proyectoSpringBoot.model.entity.ZonaBodega;
import com.proyecto.proyectoSpringBoot.model.enums.TipoZona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZonaBodegaRepository extends JpaRepository<ZonaBodega, Long> {
    List<ZonaBodega> findByBodegaId(Long bodegaId);
    List<ZonaBodega> findByTipoZona(TipoZona tipoZona);
    List<ZonaBodega> findByActivoTrue();
}
