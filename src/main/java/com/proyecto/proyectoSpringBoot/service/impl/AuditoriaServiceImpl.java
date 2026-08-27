package com.proyecto.proyectoSpringBoot.service.impl;

import com.proyecto.proyectoSpringBoot.dto.response.AuditoriaResponse;
import com.proyecto.proyectoSpringBoot.mapper.AuditoriaMapper;
import com.proyecto.proyectoSpringBoot.model.enums.TipoOperacion;
import com.proyecto.proyectoSpringBoot.repository.AuditoriaRepository;
import com.proyecto.proyectoSpringBoot.service.interfaces.IAuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditoriaServiceImpl implements IAuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final AuditoriaMapper auditoriaMapper;

    @Override
    public List<AuditoriaResponse> listarTodas() {
        return auditoriaRepository.findAll().stream()
                .map(auditoriaMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<AuditoriaResponse> listarPorUsuario(Long usuarioId) {
        return auditoriaRepository.findByUsuarioId(usuarioId).stream()
                .map(auditoriaMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<AuditoriaResponse> listarPorTipoOperacion(TipoOperacion tipo) {
        return auditoriaRepository.findByTipoOperacion(tipo).stream()
                .map(auditoriaMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<AuditoriaResponse> listarPorEntidad(String entidad) {
        return auditoriaRepository.findByEntidad(entidad).stream()
                .map(auditoriaMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<AuditoriaResponse> listarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return auditoriaRepository.findByFechaHoraBetween(inicio, fin).stream()
                .map(auditoriaMapper::toResponse).collect(Collectors.toList());
    }
}
