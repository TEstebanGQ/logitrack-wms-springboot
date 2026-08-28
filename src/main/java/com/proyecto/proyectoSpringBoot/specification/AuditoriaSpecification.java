package com.proyecto.proyectoSpringBoot.specification;

import com.proyecto.proyectoSpringBoot.model.entity.Auditoria;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class AuditoriaSpecification {

    public static Specification<Auditoria> porProducto(Long productoId) {
        return (root, query, cb) -> {
            if (productoId == null) return null;
            return cb.or(
                    cb.and(
                            cb.equal(cb.lower(root.get("entidad")), "producto"),
                            cb.equal(root.get("entidadId"), productoId)
                    ),
                    cb.like(cb.lower(root.get("descripcion")), "%producto " + productoId + "%"),
                    cb.like(cb.lower(root.get("descripcion")), "%producto: " + productoId + "%"),
                    cb.like(cb.lower(root.get("valoresNuevos")), "%\"id\":" + productoId + "%"),
                    cb.like(cb.lower(root.get("valoresNuevos")), "%\"productoid\":" + productoId + "%")
            );
        };
    }

    public static Specification<Auditoria> entFechas(LocalDateTime inicio, LocalDateTime fin) {
        return (root, query, cb) -> {
            if (inicio == null && fin == null) return null;
            if (inicio == null) return cb.lessThanOrEqualTo(root.get("fechaHora"), fin);
            if (fin == null) return cb.greaterThanOrEqualTo(root.get("fechaHora"), inicio);
            return cb.between(root.get("fechaHora"), inicio, fin);
        };
    }

    public static Specification<Auditoria> porCampoModificado(String campoModificado) {
        return (root, query, cb) -> {
            if (campoModificado == null || campoModificado.trim().isEmpty()) return null;
            String pattern = "%" + campoModificado.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("descripcion")), pattern),
                    cb.like(cb.lower(root.get("valoresNuevos")), pattern),
                    cb.like(cb.lower(root.get("valoresAnteriores")), pattern)
            );
        };
    }
}
