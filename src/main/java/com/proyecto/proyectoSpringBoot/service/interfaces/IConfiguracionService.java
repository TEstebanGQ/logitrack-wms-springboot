package com.proyecto.proyectoSpringBoot.service.interfaces;

public interface IConfiguracionService {
    String obtenerValor(String clave);
    Integer obtenerValorNumerico(String clave);
    void actualizar(String clave, String valor);
}
