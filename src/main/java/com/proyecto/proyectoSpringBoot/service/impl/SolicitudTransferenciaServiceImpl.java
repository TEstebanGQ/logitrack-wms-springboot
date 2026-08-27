package com.proyecto.proyectoSpringBoot.service.impl;

import com.proyecto.proyectoSpringBoot.dto.request.CrearSolicitudRequest;
import com.proyecto.proyectoSpringBoot.dto.request.MovimientoRequest;
import com.proyecto.proyectoSpringBoot.dto.response.SolicitudTransferenciaResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.mapper.SolicitudTransferenciaMapper;
import com.proyecto.proyectoSpringBoot.model.entity.*;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoSolicitud;
import com.proyecto.proyectoSpringBoot.model.enums.TipoMovimiento;
import com.proyecto.proyectoSpringBoot.model.enums.TipoNotificacion;
import com.proyecto.proyectoSpringBoot.repository.*;
import com.proyecto.proyectoSpringBoot.service.interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolicitudTransferenciaServiceImpl implements ISolicitudTransferenciaService {
    private final SolicitudTransferenciaRepository repository;
    private final SolicitudTransferenciaMapper mapper;
    private final BodegaRepository bodegaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final IConfiguracionService configService;
    private final INotificacionService notificacionService;
    private final IMovimientoService movimientoService;

    @Override
    @Transactional
    public SolicitudTransferenciaResponse crear(CrearSolicitudRequest request, String emailSolicitante) {
        Bodega origen = bodegaRepository.findById(request.getBodegaOrigenId()).orElseThrow(() -> new ResourceNotFoundException("Bodega origen no encontrada"));
        Bodega destino = bodegaRepository.findById(request.getBodegaDestinoId()).orElseThrow(() -> new ResourceNotFoundException("Bodega destino no encontrada"));
        if (origen.getId().equals(destino.getId())) throw new RuntimeException("Las bodegas origen y destino no pueden ser iguales");
        
        Usuario solicitante = usuarioRepository.findByEmail(emailSolicitante).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        int totalUnidades = request.getDetalles().stream().mapToInt(CrearSolicitudRequest.DetalleSolicitudRequest::getCantidad).sum();
        
        int umbral = configService.obtenerValorNumerico("UMBRAL_APROBACION");
        if (umbral == 0) umbral = 50;
        int limiteDiario = configService.obtenerValorNumerico("LIMITE_DIARIO_EMPLEADO");
        if (limiteDiario == 0) limiteDiario = 150;
        
        LocalDateTime inicioDia = LocalDateTime.now().with(LocalTime.MIN);
        Long unidadesHoyEmpleado = repository.sumUnidadesHoy(solicitante.getId(), inicioDia);
        if (unidadesHoyEmpleado == null) unidadesHoyEmpleado = 0L;
        
        boolean requiereAprobacion = totalUnidades >= umbral || (unidadesHoyEmpleado + totalUnidades) > limiteDiario;
        
        final SolicitudTransferencia solicitud = SolicitudTransferencia.builder()
                .bodegaOrigen(origen)
                .bodegaDestino(destino)
                .solicitante(solicitante)
                .observaciones(request.getObservaciones())
                .totalUnidades(totalUnidades)
                .build();
                
        final SolicitudTransferencia solicitudFinal = solicitud;
        List<SolicitudDetalleTransferencia> detalles = request.getDetalles().stream().map(d -> {
            Producto p = productoRepository.findById(d.getProductoId()).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
            return SolicitudDetalleTransferencia.builder()
                    .solicitud(solicitudFinal)
                    .producto(p)
                    .cantidad(d.getCantidad())
                    .build();
        }).collect(Collectors.toList());
        solicitud.setDetalles(detalles);
        
        SolicitudTransferencia savedSolicitud;
        if (!requiereAprobacion) {
            solicitud.setEstado(EstadoSolicitud.APROBADA_AUTOMATICA);
            savedSolicitud = repository.save(solicitud);
            ejecutarTransferencia(savedSolicitud);
            savedSolicitud.setEstado(EstadoSolicitud.EJECUTADA);
            savedSolicitud.setFechaResolucion(LocalDateTime.now());
            notificacionService.enviar(solicitante.getId(), "Tu solicitud fue aprobada automáticamente", "La solicitud de transferencia de " + totalUnidades + " unidades ha sido aprobada y ejecutada.", TipoNotificacion.EXITO);
            notificacionService.enviarATodosLosAdmins("Transferencia automática", "Transferencia automática ejecutada por " + solicitante.getNombre(), TipoNotificacion.INFO);
        } else {
            solicitud.setEstado(EstadoSolicitud.PENDIENTE);
            savedSolicitud = repository.save(solicitud);
            notificacionService.enviarATodosLosAdmins("Nueva solicitud de transferencia", "Nueva solicitud de transferencia pendiente de aprobación de " + solicitante.getNombre(), TipoNotificacion.INFO);
        }
        
        return mapper.toResponse(repository.save(savedSolicitud));
    }
    
    private void ejecutarTransferencia(SolicitudTransferencia s) {
        MovimientoRequest request = new MovimientoRequest();
        request.setTipoMovimiento(TipoMovimiento.TRANSFERENCIA);
        request.setBodegaOrigenId(s.getBodegaOrigen().getId());
        request.setBodegaDestinoId(s.getBodegaDestino().getId());
        request.setObservaciones("Transferencia automática/aprobada. Solicitud ID: " + s.getId());
        List<MovimientoRequest.DetalleRequest> dets = s.getDetalles().stream().map(d -> {
            MovimientoRequest.DetalleRequest dr = new MovimientoRequest.DetalleRequest();
            dr.setProductoId(d.getProducto().getId());
            dr.setCantidad(d.getCantidad());
            return dr;
        }).collect(Collectors.toList());
        request.setDetalles(dets);
        movimientoService.registrar(request, s.getSolicitante().getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public SolicitudTransferenciaResponse obtenerPorId(Long id) {
        return mapper.toResponse(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudTransferenciaResponse> listarTodas() {
        return repository.findAll().stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudTransferenciaResponse> listarPropias(String email) {
        Usuario u = usuarioRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return repository.findBySolicitanteId(u.getId()).stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudTransferenciaResponse> listarPendientes() {
        return repository.findByEstado(EstadoSolicitud.PENDIENTE).stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void aprobar(Long id, String emailAdmin, String observaciones) {
        SolicitudTransferencia s = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
        if (s.getEstado() != EstadoSolicitud.PENDIENTE) throw new RuntimeException("Solo se pueden aprobar solicitudes PENDIENTES");
        
        Usuario admin = usuarioRepository.findByEmail(emailAdmin).orElseThrow(() -> new ResourceNotFoundException("Admin no encontrado"));
        s.setEstado(EstadoSolicitud.APROBADA);
        s.setAprobador(admin);
        if (observaciones != null) s.setObservaciones(observaciones);
        
        ejecutarTransferencia(s);
        s.setEstado(EstadoSolicitud.EJECUTADA);
        s.setFechaResolucion(LocalDateTime.now());
        repository.save(s);
        notificacionService.enviar(s.getSolicitante().getId(), "Tu solicitud de transferencia fue aprobada", "Ha sido aprobada y ejecutada.", TipoNotificacion.EXITO);
    }

    @Override
    @Transactional
    public void rechazar(Long id, String emailAdmin, String motivo) {
        SolicitudTransferencia s = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
        if (s.getEstado() != EstadoSolicitud.PENDIENTE) throw new RuntimeException("Solo se pueden rechazar solicitudes PENDIENTES");
        
        Usuario admin = usuarioRepository.findByEmail(emailAdmin).orElseThrow(() -> new ResourceNotFoundException("Admin no encontrado"));
        s.setEstado(EstadoSolicitud.RECHAZADA);
        s.setAprobador(admin);
        s.setMotivoRechazo(motivo);
        s.setFechaResolucion(LocalDateTime.now());
        repository.save(s);
        notificacionService.enviar(s.getSolicitante().getId(), "Tu solicitud fue rechazada", motivo, TipoNotificacion.ERROR);
    }

    @Override
    @Transactional
    public void cancelar(Long id, String emailUsuario) {
        SolicitudTransferencia s = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
        Usuario u = usuarioRepository.findByEmail(emailUsuario).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        if (s.getEstado() != EstadoSolicitud.PENDIENTE) throw new RuntimeException("Solo se pueden cancelar solicitudes PENDIENTES");
        s.setEstado(EstadoSolicitud.CANCELADA);
        s.setFechaResolucion(LocalDateTime.now());
        repository.save(s);
    }
}
