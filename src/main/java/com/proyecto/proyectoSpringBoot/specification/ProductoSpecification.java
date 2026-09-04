package com.proyecto.proyectoSpringBoot.specification;

import com.proyecto.proyectoSpringBoot.model.entity.Producto;
import org.springframework.data.jpa.domain.Specification;

public class ProductoSpecification {

    public static Specification<Producto> porCategoria(String categoria) {
        return (root, query, cb) ->
                categoria == null ? null :
                        cb.like(cb.lower(root.get("categoria")), "%" + categoria.toLowerCase() + "%");
    }

    public static Specification<Producto> stockMenorQue(Integer umbral) {
        return (root, query, cb) ->
                umbral == null ? null : cb.lessThan(root.get("stock"), umbral);
    }

    public static Specification<Producto> activo() {
        return (root, query, cb) -> cb.isTrue(root.get("activo"));
    }

    public static Specification<Producto> stockEntre(Integer min, Integer max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min == null) return cb.lessThanOrEqualTo(root.get("stock"), max);
            if (max == null) return cb.greaterThanOrEqualTo(root.get("stock"), min);
            return cb.between(root.get("stock"), min, max);
        };
    }

    public static Specification<Producto> nombreContiene(String nombre) {
        return (root, query, cb) ->
                nombre == null ? null :
                        cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%");
    }
}
