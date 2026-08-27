package com.proyecto.proyectoSpringBoot.service.impl;

import com.proyecto.proyectoSpringBoot.model.entity.Configuracion;
import com.proyecto.proyectoSpringBoot.repository.ConfiguracionRepository;
import com.proyecto.proyectoSpringBoot.service.interfaces.IConfiguracionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfiguracionServiceImpl implements IConfiguracionService {
    private final ConfiguracionRepository repository;

    @Override
    public String obtenerValor(String clave) {
        return repository.findByClave(clave).map(Configuracion::getValor).orElse("0");
    }

    @Override
    public Integer obtenerValorNumerico(String clave) {
        try {
            return Integer.parseInt(obtenerValor(clave));
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void actualizar(String clave, String valor) {
        Configuracion conf = repository.findByClave(clave).orElse(new Configuracion());
        conf.setClave(clave);
        conf.setValor(valor);
        repository.save(conf);
    }
}
