package com.proyecto.proyectoSpringBoot.listener;

import com.proyecto.proyectoSpringBoot.model.entity.Auditoria;
import com.proyecto.proyectoSpringBoot.model.entity.Usuario;
import com.proyecto.proyectoSpringBoot.model.enums.TipoOperacion;
import com.proyecto.proyectoSpringBoot.repository.AuditoriaRepository;
import com.proyecto.proyectoSpringBoot.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Componente estático auxiliar para que AuditoriaEntityListener
 * (que no es Spring Bean) pueda acceder al repositorio.
 */
@Component
public class AuditoriaRegistrar {

    private static AuditoriaRepository auditoriaRepository;
    private static UsuarioRepository usuarioRepository;

    @Autowired
    public AuditoriaRegistrar(AuditoriaRepository auditoriaRepository,
                               UsuarioRepository usuarioRepository) {
        AuditoriaRegistrar.auditoriaRepository = auditoriaRepository;
        AuditoriaRegistrar.usuarioRepository = usuarioRepository;
    }

    public static void registrar(String entidad, Long entidadId, TipoOperacion tipo,
                                  String emailUsuario, String valoresAnt, String valoresNuevos) {
        try {
            Usuario usuario = usuarioRepository.findByEmail(emailUsuario).orElse(null);
            Auditoria audit = Auditoria.builder()
                    .entidad(entidad)
                    .entidadId(entidadId)
                    .tipoOperacion(tipo)
                    .fechaHora(LocalDateTime.now())
                    .usuario(usuario)
                    .valoresAnteriores(valoresAnt)
                    .valoresNuevos(valoresNuevos)
                    .descripcion(tipo.name() + " en " + entidad + (entidadId != null ? " id=" + entidadId : ""))
                    .build();
            auditoriaRepository.save(audit);
        } catch (Exception e) {
            System.err.println("[AUDITORIA] No se pudo guardar: " + e.getMessage());
        }
    }
}
