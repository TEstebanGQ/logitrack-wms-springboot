package com.proyecto.proyectoSpringBoot.repository;

import com.proyecto.proyectoSpringBoot.model.entity.AjusteInventario;
import com.proyecto.proyectoSpringBoot.model.enums.TipoAjuste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AjusteInventarioRepository extends JpaRepository<AjusteInventario, Long> {
    List<AjusteInventario> findByBodegaId(Long bodegaId);
    List<AjusteInventario> findByProductoId(Long productoId);
    List<AjusteInventario> findByTipoAjuste(TipoAjuste tipoAjuste);
    List<AjusteInventario> findAllByOrderByFechaDesc();
}
