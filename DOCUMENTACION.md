# Documento Explicativo de Arquitectura y Diseño - LogiTrack S.A.

Este documento proporciona una descripción detallada de la arquitectura de software, patrones de diseño, mecanismos de seguridad, sistema de auditoría automática y el diagrama de clases del sistema **LogiTrack S.A.**

---

## 1. Descripción de la Arquitectura

El backend de **LogiTrack S.A.** está desarrollado en **Spring Boot 3.3** con Java 17, siguiendo una **Arquitectura en Capas (Layered Architecture)** desacoplada y mantenible.

```text
┌─────────────────────────────────────────────────────────────┐
│                 CAPA DE PRESENTACIÓN (REST)                 │
│  AuthController | BodegaController | ProductoController ...  │
└──────────────────────────────┬──────────────────────────────┘
                               │ (DTOs / Validation)
┌──────────────────────────────▼──────────────────────────────┐
│                    CAPA DE NEGOCIO (SERVICE)                │
│  IBodegaService | IProductoService | IMovimientoService ... │
└──────────────────────────────┬──────────────────────────────┘
                               │ (JPQL / Specifications)
┌──────────────────────────────▼──────────────────────────────┐
│                  CAPA DE ACCESO A DATOS (JPA)               │
│  BodegaRepository | ProductoRepository | UsuarioRepository  │
└──────────────────────────────┬──────────────────────────────┘
                               │ (JDBC PostgreSQL Driver)
┌──────────────────────────────▼──────────────────────────────┐
│                   BASE DE DATOS RELACIONAL                  │
│                     PostgreSQL / Supabase                   │
└─────────────────────────────────────────────────────────────┘
```

### Componentes de la Arquitectura:

1. **Controllers (`com.proyecto.proyectoSpringBoot.controller`)**:
   - Exponen endpoints REST protegidos por Spring Security.
   - Reciben y retornan objetos DTOs (`JwtResponse`, `MovimientoRequest`, etc.).
   - Utilizan anotaciones `@Valid` para validación de entrada Bean Validation.

2. **Services (`com.proyecto.proyectoSpringBoot.service`)**:
   - Definen interfaces (`interfaces/`) e implementaciones (`impl/`).
   - Manejan transacciones `@Transactional`, reglas de negocio y validación de stock.

3. **Repositories (`com.proyecto.proyectoSpringBoot.repository`)**:
   - Heredan de `JpaRepository<T, ID>` y `JpaSpecificationExecutor<T>`.
   - Permiten consultas avanzadas y filtros dinámicos (Stock bajo, fechas BETWEEN, auditorías).

4. **Entities (`com.proyecto.proyectoSpringBoot.model.entity`)**:
   - Mapeo ORM mediante Hibernate/JPA.
   - Anotadas con `@Entity`, `@Table` e integradas con `@EntityListeners`.

---

## 2. Diagrama de Clases UML (Mermaid)

```mermaid
classDiagram
    class Usuario {
        +Long id
        +String nombre
        +String apellido
        +String email
        +String password
        +RolUsuario rol
        +Boolean activo
        +LocalDateTime createdAt
    }

    class Bodega {
        +Long id
        +String nombre
        +String ubicacion
        +Integer capacidad
        +String encargado
        +Boolean activo
    }

    class Producto {
        +Long id
        +String nombre
        +String categoria
        +Integer stock
        +BigDecimal precio
        +String descripcion
        +Boolean activo
    }

    class Movimiento {
        +Long id
        +TipoMovimiento tipoMovimiento
        +LocalDateTime fecha
        +String observaciones
        +Usuario usuario
        +Bodega bodegaOrigen
        +Bodega bodegaDestino
        +List~MovimientoDetalle~ detalles
    }

    class MovimientoDetalle {
        +Long id
        +Movimiento movimiento
        +Producto producto
        +Integer cantidad
        +BigDecimal precioUnitario
    }

    class InventarioBodega {
        +Long id
        +Bodega bodega
        +Producto producto
        +Integer stockActual
        +LocalDateTime ultimaActualizacion
    }

    class Auditoria {
        +Long id
        +String entidad
        +Long entidadId
        +TipoOperacion tipoOperacion
        +LocalDateTime fechaHora
        +Long usuarioId
        +String valoresAnteriores
        +String valoresNuevos
        +String ipAddress
        +String descripcion
    }

    class RolUsuario {
        <<enumeration>>
        ADMIN
        EMPLEADO
    }

    class TipoMovimiento {
        <<enumeration>>
        ENTRADA
        SALIDA
        TRANSFERENCIA
    }

    class TipoOperacion {
        <<enumeration>>
        INSERT
        UPDATE
        DELETE
    }

    Usuario "1" -- "0..*" Movimiento : realiza
    Bodega "1" -- "0..*" Movimiento : origen
    Bodega "1" -- "0..*" Movimiento : destino
    Movimiento "1" -- "1..*" MovimientoDetalle : contiene
    Producto "1" -- "0..*" MovimientoDetalle : incluido_en
    Bodega "1" -- "0..*" InventarioBodega : almacena
    Producto "1" -- "0..*" InventarioBodega : registrado_en
    Usuario --> RolUsuario
    Movimiento --> TipoMovimiento
    Auditoria --> TipoOperacion
```

---

## 3. Auditoría Automática con JPA EntityListeners

El registro auditable de operaciones se realiza de forma automática e invisible para la capa de servicios utilizando JPA Lifecycle Callbacks:

- **`@EntityListeners(AuditoriaEntityListener.class)`**: Anotado en las entidades del sistema (`Bodega`, `Producto`, `Movimiento`, `Usuario`).
- **Hooks de Evento**:
  - `@PostPersist`: Captura operaciones `INSERT`.
  - `@PostUpdate`: Captura operaciones `UPDATE`.
  - `@PostRemove`: Captura operaciones `DELETE`.
- **`AuditoriaRegistrar`**: Componente encargado de serializar los valores a JSON y persistirlos en la tabla `auditoria` asociando el ID del usuario autenticado en el contexto de seguridad (`SecurityContextHolder`).

---

## 4. Autenticación y Seguridad JWT

La seguridad del sistema está construida sobre **Spring Security 6** y tokens **JSON Web Tokens (JWT)** estateless:

1. **Generación del Token**:
   - Al invocar POST `/auth/login`, el `AuthenticationManager` valida las credenciales introducidas contra la base de datos (con contraseñas encriptadas en **BCrypt**).
   - Si la autenticación es exitosa, `JwtUtil` genera un token firmado algorítmicamente (HMAC-SHA256) que contiene el `email` del usuario y su fecha de expiración.

2. **Filtro de Intercepción (`JwtAuthFilter`)**:
   - Cada solicitud entrante pasa por `JwtAuthFilter` (subclase de `OncePerRequestFilter`).
   - Extrae el encabezado `Authorization: Bearer <token>`.
   - Valida la firma del token y el estado del usuario.
   - Establece el objeto `UsernamePasswordAuthenticationToken` en el `SecurityContextHolder`.

3. **Ejemplo de Token y Encabezado HTTP**:
   ```http
   GET /bodegas HTTP/1.1
   Host: localhost:8081
   Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBsb2dpdHJhY2suY29tIiwiaWF0IjoxNzA4ODcwMDAwLCJleHAiOjE3MDg5NTY0MDB9.SIGNATURE
   ```

---

## 5. Servicio de Notificaciones y Correos de Bienvenida (Gmail SMTP)

LogiTrack cuenta con un módulo de correos corporativos automáticos impulsado por `spring-boot-starter-mail`, `JavaMailSender` y plantillas HTML dinámicas adaptadas según el `RolUsuario`:

1. **Disparo en Registro**: Al registrarse un usuario (POST `/api/auth/register`), el `AuthController` delega a `IEmailService.enviarCorreoBienvenida(usuario)`.
2. **Ejecución Asíncrona (`@Async`)**: El envío ocurre en un pool de hilos independiente (`AsyncConfig` / `mailTaskExecutor`), respondiendo de inmediato al cliente HTTP.
3. **Plantillas HTML Adaptadas por Rol (`EmailTemplateBuilder`)**:
   - `ADMIN`: Insignia púrpura, credenciales de administración, auditoría global y seguridad.
   - `SUPERVISOR`: Insignia ámbar, gestión de picking, conteos cíclicos y control de calidad.
   - `GERENTE_LOGISTICA`: Insignia verde esmeralda, guías de despacho, transportadoras y KPIs ABC.
   - `JEFE_COMPRAS`: Insignia azul cielo, órdenes de compra y gestión de proveedores.
   - `EMPLEADO`: Insignia azul real, preparación de picking asistido y serialización.
4. **Tolerancia a Fallos**: Si las credenciales SMTP no están presentes o hay cortes de red externos, el registro no se bloquea y se registra el error de forma segura en los logs.

