package com.proyecto.proyectoSpringBoot.service.impl;

import com.proyecto.proyectoSpringBoot.dto.request.CrearClienteRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ClienteResponse;
import com.proyecto.proyectoSpringBoot.dto.response.MovimientoResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.mapper.ClienteMapper;
import com.proyecto.proyectoSpringBoot.model.entity.Cliente;
import com.proyecto.proyectoSpringBoot.repository.ClienteRepository;
import com.proyecto.proyectoSpringBoot.repository.MovimientoRepository;
import com.proyecto.proyectoSpringBoot.service.interfaces.IClienteService;
import com.proyecto.proyectoSpringBoot.mapper.MovimientoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements IClienteService {
    private final ClienteRepository repository;
    private final ClienteMapper mapper;
    private final MovimientoRepository movimientoRepository;
    private final MovimientoMapper movimientoMapper;

    @Override
    @Transactional
    public ClienteResponse crear(CrearClienteRequest request) {
        if (request.getRuc() != null && repository.existsByRuc(request.getRuc())) {
            throw new RuntimeException("Ya existe un cliente con este RUC");
        }
        Cliente c = mapper.toEntity(request);
        return mapper.toResponse(repository.save(c));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return listar(true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> listar(boolean soloActivos) {
        if (soloActivos) {
            return repository.findByActivoTrue().stream().map(mapper::toResponse).collect(Collectors.toList());
        }
        return repository.findAll().stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse obtenerPorId(Long id) {
        return mapper.toResponse(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + id)));
    }

    @Override
    @Transactional
    public ClienteResponse actualizar(Long id, CrearClienteRequest request) {
        Cliente c = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + id));
        if (request.getRuc() != null && !request.getRuc().equals(c.getRuc()) && repository.existsByRuc(request.getRuc())) {
            throw new RuntimeException("Ya existe otro cliente con este RUC");
        }
        c.setNombre(request.getNombre());
        c.setRuc(request.getRuc());
        c.setTelefono(request.getTelefono());
        c.setEmail(request.getEmail());
        c.setDireccion(request.getDireccion());
        return mapper.toResponse(repository.save(c));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Cliente c = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + id));
        c.setActivo(false);
        repository.save(c);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosCliente(Long id) {
        repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + id));
        return movimientoRepository.findByClienteId(id).stream()
                .map(movimientoMapper::toResponse)
                .collect(Collectors.toList());
    }
}
