package com.proyecto.proyectoSpringBoot.service.impl;

import com.proyecto.proyectoSpringBoot.dto.request.ConteoCiclicoDetalleRequest;
import com.proyecto.proyectoSpringBoot.dto.request.ConteoCiclicoRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ConteoCiclicoResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.mapper.ConteoCiclicoMapper;
import com.proyecto.proyectoSpringBoot.model.entity.*;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoConteo;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoLineaConteo;
import com.proyecto.proyectoSpringBoot.model.enums.TipoAjuste;
import com.proyecto.proyectoSpringBoot.repository.*;
import com.proyecto.proyectoSpringBoot.service.interfaces.IConteoCiclicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ConteoCiclicoServiceImpl implements IConteoCiclicoService {

    private final ConteoCiclicoRepository conteoCiclicoRepository;
    private final ConteoCiclicoDetalleRepository detalleRepository;
    private final BodegaRepository bodegaRepository;
    private final ZonaBodegaRepository zonaBodegaRepository;
    private final ProductoRepository productoRepository;
    private final UbicacionBodegaRepository ubicacionBodegaRepository;
    private final UsuarioRepository usuarioRepository;
    private final InventarioBodegaRepository inventarioBodegaRepository;
    private final AjusteInventarioRepository ajusteInventarioRepository;
    private final ConteoCiclicoMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<ConteoCiclicoResponse> listarTodos() {
        return conteoCiclicoRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConteoCiclicoResponse> listarPorBodega(Long bodegaId) {
        return conteoCiclicoRepository.findByBodegaId(bodegaId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConteoCiclicoResponse> listarPorEstado(EstadoConteo estado) {
        return conteoCiclicoRepository.findByEstado(estado).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ConteoCiclicoResponse obtenerPorId(Long id) {
        ConteoCiclico c = conteoCiclicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conteo cíclico no encontrado con id: " + id));
        return mapper.toResponse(c);
    }

    @Override
    public ConteoCiclicoResponse crear(ConteoCiclicoRequest request, String supervisorEmail) {
        Bodega bodega = bodegaRepository.findById(request.getBodegaId())
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con id: " + request.getBodegaId()));

        ZonaBodega zona = null;
        if (request.getZonaId() != null) {
            zona = zonaBodegaRepository.findById(request.getZonaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Zona no encontrada con id: " + request.getZonaId()));
        }

        Usuario supervisor = usuarioRepository.findByEmail(supervisorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + supervisorEmail));

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String codigoConteo = "AUD-" + timestamp;

        ConteoCiclico conteo = ConteoCiclico.builder()
                .codigoConteo(codigoConteo)
                .bodega(bodega)
                .zona(zona)
                .fechaProgramada(request.getFechaProgramada() != null ? request.getFechaProgramada() : LocalDate.now())
                .supervisor(supervisor)
                .observaciones(request.getObservaciones())
                .estado(EstadoConteo.PROGRAMADO)
                .detalles(new ArrayList<>())
                .build();

        if (request.getDetalles() != null && !request.getDetalles().isEmpty()) {
            for (ConteoCiclicoDetalleRequest detReq : request.getDetalles()) {
                Producto prod = productoRepository.findById(detReq.getProductoId())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + detReq.getProductoId()));

                UbicacionBodega ubic = null;
                if (detReq.getUbicacionId() != null) {
                    ubic = ubicacionBodegaRepository.findById(detReq.getUbicacionId()).orElse(null);
                }

                int stockSistema = inventarioBodegaRepository.findByBodegaIdAndProductoId(bodega.getId(), prod.getId())
                        .map(InventarioBodega::getStockActual).orElse(prod.getStock());

                int stockFisico = detReq.getStockFisico() != null ? detReq.getStockFisico() : stockSistema;

                ConteoCiclicoDetalle det = ConteoCiclicoDetalle.builder()
                        .conteo(conteo)
                        .producto(prod)
                        .ubicacion(ubic)
                        .stockSistema(stockSistema)
                        .stockFisico(stockFisico)
                        .diferencia(stockFisico - stockSistema)
                        .estadoLinea(EstadoLineaConteo.PENDIENTE)
                        .notas(detReq.getNotas())
                        .build();

                conteo.getDetalles().add(det);
            }
        }

        return mapper.toResponse(conteoCiclicoRepository.save(conteo));
    }

    @Override
    public ConteoCiclicoResponse registrarConteoFisico(Long id, Long detalleId, Integer stockFisico, String notas) {
        ConteoCiclico conteo = conteoCiclicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conteo cíclico no encontrado con id: " + id));

        ConteoCiclicoDetalle detalle = detalleRepository.findById(detalleId)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de conteo no encontrado con id: " + detalleId));

        detalle.setStockFisico(stockFisico);
        detalle.setDiferencia(stockFisico - detalle.getStockSistema());
        detalle.setNotas(notas);
        detalle.setEstadoLinea(detalle.getDiferencia() == 0 ? EstadoLineaConteo.CONCILIADO : EstadoLineaConteo.RECONTAR);
        detalleRepository.save(detalle);

        conteo.setEstado(EstadoConteo.EN_PROCESO);
        conteo.setFechaEjecucion(LocalDateTime.now());
        return mapper.toResponse(conteoCiclicoRepository.save(conteo));
    }

    @Override
    public ConteoCiclicoResponse conciliarYCerrar(Long id, String supervisorEmail) {
        ConteoCiclico conteo = conteoCiclicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conteo cíclico no encontrado con id: " + id));

        Usuario supervisor = usuarioRepository.findByEmail(supervisorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + supervisorEmail));

        for (ConteoCiclicoDetalle det : conteo.getDetalles()) {
            if (det.getStockFisico() != null && det.getDiferencia() != 0) {
                // Generar ajuste de inventario automático
                AjusteInventario ajuste = AjusteInventario.builder()
                        .bodega(conteo.getBodega())
                        .producto(det.getProducto())
                        .tipoAjuste(TipoAjuste.CONTEO_FISICO)
                        .cantidadAnterior(det.getStockSistema())
                        .cantidadNueva(det.getStockFisico())
                        .diferencia(det.getDiferencia())
                        .justificacion("Conciliación automática por auditoría cíclica " + conteo.getCodigoConteo())
                        .usuario(supervisor)
                        .fecha(LocalDateTime.now())
                        .build();
                ajusteInventarioRepository.save(ajuste);

                // Actualizar inventario en bodega
                inventarioBodegaRepository.findByBodegaIdAndProductoId(conteo.getBodega().getId(), det.getProducto().getId())
                        .ifPresent(inv -> {
                            inv.setStockActual(det.getStockFisico());
                            inventarioBodegaRepository.save(inv);
                        });

                det.setEstadoLinea(EstadoLineaConteo.AJUSTADO);
            } else {
                det.setEstadoLinea(EstadoLineaConteo.CONCILIADO);
            }
            detalleRepository.save(det);
        }

        conteo.setEstado(EstadoConteo.CERRADO);
        conteo.setFechaEjecucion(LocalDateTime.now());
        return mapper.toResponse(conteoCiclicoRepository.save(conteo));
    }

    @Override
    public void eliminar(Long id) {
        ConteoCiclico c = conteoCiclicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conteo cíclico no encontrado con id: " + id));
        conteoCiclicoRepository.delete(c);
    }
}
