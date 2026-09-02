package com.proyecto.proyectoSpringBoot.repository;

import com.proyecto.proyectoSpringBoot.model.entity.OrdenCompra;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoOrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {
    Optional<OrdenCompra> findByCodigoOrden(String codigoOrden);
    boolean existsByCodigoOrden(String codigoOrden);
    List<OrdenCompra> findByProveedorId(Long proveedorId);
    List<OrdenCompra> findByBodegaDestinoId(Long bodegaId);
    List<OrdenCompra> findByEstado(EstadoOrdenCompra estado);
    List<OrdenCompra> findAllByOrderByFechaSolicitudDesc();

    /**
     * Obtiene el siguiente valor consecutivo de la secuencia de base de datos para la generación atómica de códigos de orden.
     */
    @Query(value = "SELECT nextval('orden_compra_seq')", nativeQuery = true)
    Long nextOrdenCompraSeq();
}
