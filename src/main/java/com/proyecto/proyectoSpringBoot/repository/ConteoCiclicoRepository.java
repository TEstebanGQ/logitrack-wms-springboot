package com.proyecto.proyectoSpringBoot.repository;

import com.proyecto.proyectoSpringBoot.model.entity.ConteoCiclico;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoConteo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConteoCiclicoRepository extends JpaRepository<ConteoCiclico, Long> {
    Optional<ConteoCiclico> findByCodigoConteo(String codigoConteo);
    List<ConteoCiclico> findByBodegaId(Long bodegaId);
    List<ConteoCiclico> findByEstado(EstadoConteo estado);
}
