package com.proyecto.proyectoSpringBoot.service.impl;

import com.proyecto.proyectoSpringBoot.dto.response.AlertaStockResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.mapper.AlertaStockMapper;
import com.proyecto.proyectoSpringBoot.model.entity.AlertaStock;
import com.proyecto.proyectoSpringBoot.model.entity.Bodega;
import com.proyecto.proyectoSpringBoot.model.entity.Producto;
import com.proyecto.proyectoSpringBoot.model.entity.Usuario;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoAlerta;
import com.proyecto.proyectoSpringBoot.model.enums.TipoNotificacion;
import com.proyecto.proyectoSpringBoot.repository.AlertaStockRepository;
import com.proyecto.proyectoSpringBoot.repository.UsuarioRepository;
import com.proyecto.proyectoSpringBoot.service.interfaces.IAlertaStockService;
import com.proyecto.proyectoSpringBoot.service.interfaces.INotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertaStockServiceImpl implements IAlertaStockService {
    private final AlertaStockRepository repository;
    private final AlertaStockMapper mapper;
    private final INotificacionService notificacionService;
    private final UsuarioRepository usuarioRepository;
    private final com.proyecto.proyectoSpringBoot.repository.InventarioBodegaRepository inventarioBodegaRepository;

    @Override
    @Transactional
    public void verificarYGenerarAlerta(Producto producto, Bodega bodega, Integer stockActual) {
        if (stockActual < producto.getStockMinimo() && !repository.existsByProductoIdAndBodegaIdAndEstado(producto.getId(), bodega.getId(), EstadoAlerta.PENDIENTE)) {
            AlertaStock alerta = AlertaStock.builder()
                    .producto(producto)
                    .bodega(bodega)
                    .stockActual(stockActual)
                    .stockMinimo(producto.getStockMinimo())
                    .estado(EstadoAlerta.PENDIENTE)
                    .build();
            repository.save(alerta);
            String mensaje = "El stock del producto " + producto.getNombre() + " en la bodega " + bodega.getNombre() + " está por debajo del mínimo (" + stockActual + " / " + producto.getStockMinimo() + ").";
            notificacionService.enviarATodosLosAdmins("⚠️ Stock bajo: " + producto.getNombre(), mensaje, TipoNotificacion.ALERTA);
        }
    }

    @Transactional
    public void escanearYGenerarAlertas() {
        // 1. Auto-resolver alertas pendientes y limpiar notificaciones si el stock ya se recuperó o la bodega fue desasignada
        List<AlertaStock> pendientes = repository.findByEstado(EstadoAlerta.PENDIENTE);
        for (AlertaStock a : pendientes) {
            var optInv = inventarioBodegaRepository.findByBodegaIdAndProductoId(a.getBodega().getId(), a.getProducto().getId());
            if (optInv.isEmpty() || optInv.get().getStockActual() >= a.getProducto().getStockMinimo()) {
                a.setEstado(EstadoAlerta.RESUELTA);
                a.setFechaResuelta(LocalDateTime.now());
                repository.save(a);
                notificacionService.limpiarNotificacionesObsoletas(a.getProducto().getNombre(), a.getBodega().getNombre());
            }
        }

        // 2. Generar alertas para los inventarios que tengan stock por debajo del mínimo
        List<com.proyecto.proyectoSpringBoot.model.entity.InventarioBodega> bajos = inventarioBodegaRepository.findInventariosConStockBajo();
        for (var inv : bajos) {
            verificarYGenerarAlerta(inv.getProducto(), inv.getBodega(), inv.getStockActual());
        }
    }

    @Override
    @Transactional
    public List<AlertaStockResponse> listarTodas() {
        escanearYGenerarAlertas();
        return repository.findAll().stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<AlertaStockResponse> listarPendientes() {
        escanearYGenerarAlertas();
        return repository.findByEstado(EstadoAlerta.PENDIENTE).stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaStockResponse> listarPorProducto(Long productoId) {
        return repository.findByProductoId(productoId).stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void resolver(Long alertaId, String emailUsuario) {
        AlertaStock alerta = repository.findById(alertaId).orElseThrow(() -> new ResourceNotFoundException("Alerta no encontrada"));
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        alerta.setEstado(EstadoAlerta.RESUELTA);
        alerta.setFechaResuelta(LocalDateTime.now());
        alerta.setResueltaPor(usuario);
        repository.save(alerta);
    }

    @Override
    @Transactional
    public void eliminar(Long alertaId) {
        AlertaStock alerta = repository.findById(alertaId).orElseThrow(() -> new ResourceNotFoundException("Alerta no encontrada con id: " + alertaId));
        repository.delete(alerta);
    }
}

