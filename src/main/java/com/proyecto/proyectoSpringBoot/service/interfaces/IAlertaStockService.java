package com.proyecto.proyectoSpringBoot.service.interfaces;

import com.proyecto.proyectoSpringBoot.dto.response.AlertaStockResponse;
import com.proyecto.proyectoSpringBoot.model.entity.Bodega;
import com.proyecto.proyectoSpringBoot.model.entity.Producto;
import java.util.List;

public interface IAlertaStockService {
    void verificarYGenerarAlerta(Producto producto, Bodega bodega, Integer stockActual);
    List<AlertaStockResponse> listarTodas();
    List<AlertaStockResponse> listarPendientes();
    List<AlertaStockResponse> listarPorProducto(Long productoId);
    void resolver(Long alertaId, String emailUsuario);
    void eliminar(Long alertaId);
}
