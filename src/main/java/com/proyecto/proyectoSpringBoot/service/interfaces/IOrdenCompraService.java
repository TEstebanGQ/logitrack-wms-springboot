package com.proyecto.proyectoSpringBoot.service.interfaces;

import com.proyecto.proyectoSpringBoot.dto.request.CrearOrdenCompraRequest;
import com.proyecto.proyectoSpringBoot.dto.response.OrdenCompraResponse;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoOrdenCompra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IOrdenCompraService {
    OrdenCompraResponse crearOrden(CrearOrdenCompraRequest request, String emailUsuario);
    List<OrdenCompraResponse> listarTodas();
    Page<OrdenCompraResponse> listarTodas(Pageable pageable);
    List<OrdenCompraResponse> listarPorProveedor(Long proveedorId);
    List<OrdenCompraResponse> listarPorEstado(EstadoOrdenCompra estado);
    OrdenCompraResponse obtenerPorId(Long id);
    OrdenCompraResponse aprobarOrden(Long id, String emailUsuario);
    OrdenCompraResponse cancelarOrden(Long id, String motivo, String emailUsuario);
    OrdenCompraResponse recibirOrden(Long id, String emailUsuario);
}
