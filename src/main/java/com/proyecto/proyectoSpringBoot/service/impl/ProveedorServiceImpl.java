package com.proyecto.proyectoSpringBoot.service.impl;

import com.proyecto.proyectoSpringBoot.dto.request.CrearProveedorRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ProveedorResponse;
import com.proyecto.proyectoSpringBoot.dto.response.MovimientoResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.mapper.ProveedorMapper;
import com.proyecto.proyectoSpringBoot.model.entity.Proveedor;
import com.proyecto.proyectoSpringBoot.repository.ProveedorRepository;
import com.proyecto.proyectoSpringBoot.repository.MovimientoRepository;
import com.proyecto.proyectoSpringBoot.service.interfaces.IProveedorService;
import com.proyecto.proyectoSpringBoot.mapper.MovimientoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements IProveedorService {
    private final ProveedorRepository repository;
    private final ProveedorMapper mapper;
    private final MovimientoRepository movimientoRepository;
    private final MovimientoMapper movimientoMapper;

    @Override
    @Transactional
    public ProveedorResponse crear(CrearProveedorRequest request) {
        if (request.getRuc() != null && repository.existsByRuc(request.getRuc())) {
            throw new RuntimeException("Ya existe un proveedor con este RUC");
        }
        Proveedor p = mapper.toEntity(request);
        return mapper.toResponse(repository.save(p));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorResponse> listar() {
        return listar(true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorResponse> listar(boolean soloActivos) {
        if (soloActivos) {
            return repository.findByActivoTrue().stream().map(mapper::toResponse).collect(Collectors.toList());
        }
        return repository.findAll().stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorResponse obtenerPorId(Long id) {
        return mapper.toResponse(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado: " + id)));
    }

    @Override
    @Transactional
    public ProveedorResponse actualizar(Long id, CrearProveedorRequest request) {
        Proveedor p = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado: " + id));
        if (request.getRuc() != null && !request.getRuc().equals(p.getRuc()) && repository.existsByRuc(request.getRuc())) {
            throw new RuntimeException("Ya existe otro proveedor con este RUC");
        }
        p.setNombre(request.getNombre());
        p.setRuc(request.getRuc());
        p.setTelefono(request.getTelefono());
        p.setEmail(request.getEmail());
        p.setDireccion(request.getDireccion());
        return mapper.toResponse(repository.save(p));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Proveedor p = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado: " + id));
        p.setActivo(false);
        repository.save(p);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosProveedor(Long id) {
        repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado: " + id));
        return movimientoRepository.findByProveedorId(id).stream()
                .map(movimientoMapper::toResponse)
                .collect(Collectors.toList());
    }
}
