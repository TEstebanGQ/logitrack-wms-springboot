package com.proyecto.proyectoSpringBoot.repository;

import com.proyecto.proyectoSpringBoot.model.entity.ConteoCiclicoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConteoCiclicoDetalleRepository extends JpaRepository<ConteoCiclicoDetalle, Long> {
    List<ConteoCiclicoDetalle> findByConteoId(Long conteoId);
}
