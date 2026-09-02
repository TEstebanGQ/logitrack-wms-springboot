# 📦 LogiTrack S.A. — Sistema WMS & Gestión Completa de Bodegas e Inventario

> 🌐 **Plataforma en Vivo (Entorno de Producción HTTPS):**  
> 👉 **[https://logitrack.34.70.8.165.sslip.io](https://logitrack.34.70.8.165.sslip.io)**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

**LogiTrack S.A.** es un sistema de gestión de almacenes e inventarios de clase empresarial (**Warehouse Management System - WMS**). Diseñado para garantizar **trazabilidad del 100% en tiempo real** de todas las operaciones logísticas: recepción de mercancía, almacenamiento multisitio, traslados entre bodegas, control de lotes, picking, despachos, órdenes de compra y auditorías físicas de stock.

Proyecto académico desarrollado por **Tomas Esteban González Quintero** (Campuslands) como sistema backend completo con frontend integrado, evolucionado hasta un WMS de alcance empresarial.


## 📑 Tabla de Contenido

1. [Descripción Detallada de la Lógica de Negocio](#-descripción-detallada-de-la-lógica-de-negocio)
2. [Matriz de Roles y Jerarquía de Seguridad](#-matriz-de-roles-y-jerarquía-de-seguridad)
3. [Credenciales Predeterminadas del Sistema](#-credenciales-predeterminadas-del-sistema)
4. [Arquitectura Técnica y Tecnologías](#️-arquitectura-técnica-y-tecnologías)
5. [Estructura del Proyecto](#-estructura-del-proyecto)
6. [Catálogo de Endpoints de la API](#-catálogo-de-endpoints-de-la-api)
7. [Puesta en Marcha (Ejecución Local)](#-puesta-en-marcha-ejecución-local)
8. [Variables de Entorno](#-variables-de-entorno)
9. [Documentación de la API (Swagger)](#-documentación-de-la-api-swagger)
10. [Despliegue en la Nube](#-despliegue-en-la-nube-google-cloud-platform)
11. [Licencia](#-licencia)

---

## 🏢 Descripción Detallada de la Lógica de Negocio

El sistema organiza la operación logística de LogiTrack S.A. en los siguientes módulos funcionales interconectados:

### 1. 🏬 Infraestructura de Almacenamiento Multisitio
- **Bodegas Principales y Auxiliares:** Configuración de múltiples centros de distribución con capacidad máxima, dirección y estado operativo.
- **Zonas de Bodega:** Segmentación interna por áreas operativas: *Recepción, Despacho, Almacén General, Zona de Refrigeración y Cuarentena*.
- **Ubicaciones Físicas & Estantería:** Trazabilidad exacta a nivel de *Pasillos, Racks, Estantes y Gavetas/Palets*, permitiendo saber en qué casilla física está almacenado cada producto.

### 2. 📦 Catálogo de Productos y Trazabilidad por Número de Serie
- **Catálogo Maestro:** Gestión de productos con SKU, descripción, precio, categoría, stock mínimo de reorden y unidad de medida (Kilos, Litros, Cajas, Unidades).
- **Números de Serie Unitarios:** Seguimiento individual y unitario para productos tecnológicos o de alto valor, registrando la historia de cada número de serie desde la recepción hasta su venta.

### 3. 🔄 Control de Movimientos de Inventario
- **Entradas (`ENTRADA`):** Registro de ingresos de mercancía por compras a proveedores, devoluciones o producción.
- **Salidas (`SALIDA`):** Egreso de inventario por venta, despacho de pedidos o consumo interno.
- **Transferencias (`TRANSFERENCIA`):** Traslado controlado de stock entre diferentes bodegas o ubicaciones internas, actualizando saldos en tiempo real.

### 4. 🏷️ Gestión de Lotes y Control de Vencimientos
- **Control por Lote:** Agrupación de productos por código de lote con fecha de fabricación y fecha de caducidad.
- **FEFO (First Expired, First Out):** Consulta dedicada para priorizar el despacho de los lotes más próximos a vencer.
- **Estados de Lote:**
  - `DISPONIBLE`: Mercancía apta para venta o distribución.
  - `CUARENTENA`: Mercancía bloqueada temporalmente para inspección de calidad.
  - `AGOTADO`: Lote sin existencia física.
  - `VENCIDO`: Mercancía caducada bloqueada automáticamente para prevenir despachos.

### 5. 📋 Conteos Cíclicos y Ajustes de Inventario
- **Auditorías de Stock (Conteos Cíclicos):** Creación de órdenes de conteo físico por bodega para validar la presencia real de productos.
- **Conciliación de Diferencias:** Comparación automática entre el inventario físico contado y el saldo registrado en el sistema.
- **Ajustes de Inventario:** Registro justificado de faltantes, sobrantes o productos dañados durante el proceso de auditoría.

### 6. 🛒 Compras y Abastecimiento a Proveedores
- **Directorio de Proveedores:** Registro de contactos, RUT/NIT y condiciones comerciales.
- **Órdenes de Compra:** Gestión del flujo completo de abastecimiento (`PENDIENTE` ➔ `APROBADA` ➔ `RECIBIDA` ➔ `CANCELADA`), calculando montos y actualizando el inventario al recibir la mercancía.

### 7. 🚛 Pedidos, Tareas de Picking y Guías de Despacho
- **Pedidos de Clientes:** Registro de solicitudes de venta con desglose de ítems, precios y clientes.
- **Tareas de Picking:** Asignación de rutas de recolección a operadores de bodega para la preparación física de pedidos.
- **Guías de Despacho & Transportadoras:** Generación de guías de remisión oficiales vinculadas a empresas de transporte para la entrega final al cliente.

### 8. 🔔 Notificaciones Internas y Alertas en Tiempo Real
- **Alertas de Stock Bajo:** Detección automática cuando un producto cae por debajo de su stock mínimo configurado.
- **Notificaciones del Sistema:** Registro de alertas en la base de datos con indicador visual (insignia de la campana) en la barra superior del usuario.

### 9. 📧 Servicio de Notificaciones Automáticas por Correo (Gmail SMTP)
- **Correos de Bienvenida:** Envío de correo electrónico profesional adaptado al rol del usuario registrado.
- **Alerta de Cambio de Rol:** Notificación automática al usuario cuando un Administrador actualiza su nivel de acceso.
- **Reporte Ejecutivo Diario bajo Demanda:** Generación y envío por correo a todos los directivos (`SUPER_ADMIN`, `ADMIN`, `GERENTE_LOGISTICA`) con el consolidado diario de movimientos, alertas y auditorías.

### 10. 🧾 Reportes y Exportación
- **Reportes analíticos:** Movimientos por rango de fechas, auditorías del sistema y clasificación ABC de productos por rotación/valor.
- **Exportación a Excel y PDF:** Generación de reportes descargables (Apache POI para `.xlsx` y OpenPDF para `.pdf`) tanto de movimientos como de auditorías.

### 11. 🕵️ Auditoría Automática de Cambios
- **Listener de entidades JPA (`AuditoriaEntityListener`):** Registra automáticamente cada operación `INSERT` / `UPDATE` / `DELETE` sobre las entidades críticas del sistema, guardando usuario responsable, entidad afectada, tipo de operación y marca de tiempo.
- **Consulta de auditoría:** Filtrable por usuario, tipo de operación, entidad y rango de fechas.

### 12. 🔑 Autenticación Dual & Seguridad
- **Autenticación Local:** Inicio de sesión con correo y contraseña cifrada con `BCrypt` y tokens `JWT` de 256 bits.
- **Acceder con Google (OAuth2):** Autenticación en 1 clic con Google Identity Services. Si es un usuario nuevo, le solicita definir obligatoriamente su contraseña personal y se le asigna de forma segura el rol **`EMPLEADO`**.

---

## 👑 Matriz de Roles y Jerarquía de Seguridad

La plataforma implementa la **Jerarquía de Roles de Spring Security 6** (`RoleHierarchy`), garantizando que los roles superiores hereden todas las atribuciones de los roles inferiores:

```text
ROLE_SUPER_ADMIN > ROLE_ADMIN > ROLE_GERENTE_LOGISTICA > ROLE_SUPERVISOR > ROLE_JEFE_COMPRAS > ROLE_EMPLEADO
```

| Rol | Atribuciones y Permisos en la Plataforma |
| :--- | :--- |
| **`SUPER_ADMIN`** | **Super Administrador Global.** Acceso total absoluto a todos los módulos, auditorías del sistema, gestión de administradores y recepción/envío de reportes ejecutivos. |
| **`ADMIN`** | **Administrador del Sistema.** Creación y edición de usuarios, parametrización de bodegas, categorías, ubicaciones y catálogo de productos. |
| **`GERENTE_LOGISTICA`** | **Gerencia Logística.** Emisión de guías de despacho, envío de reportes ejecutivos diarios por correo, supervisión de pedidos y analítica de bodegas. |
| **`SUPERVISOR`** | **Supervisión de Operaciones.** Planificación de conteos cíclicos, conciliación de diferencias de stock, control de cuarentena y aprobación de ajustes. |
| **`JEFE_COMPRAS`** | **Jefatura de Abastecimiento.** Gestión de proveedores, emisión y aprobación de órdenes de compra y recepción de lotes de mercancía. |
| **`EMPLEADO`** | **Operador de Bodega.** Registro de entradas, salidas, transferencias de mercancía y ejecución de tareas de recolección (Picking). |

---

## 🔑 Credenciales Predeterminadas del Sistema

| Rol | Correo Electrónico | Contraseña |
| :--- | :--- | :--- |
| **Super Administrador** | `tomasestebangonzalezquintero@gmail.com` | `admin123` *(o botón **Acceder con Google**)* |
| **Administrador** | `admin@logitrack.com` | `admin123` |
| **Supervisor** | `laura@logitrack.com` | `empleado123` |
| **Gerente Logística** | `sofia@logitrack.com` | `empleado123` |
| **Jefe de Compras** | `pedro@logitrack.com` | `empleado123` |
| **Empleado Operativo** | `carlos@logitrack.com` | `empleado123` |

> ⚠️ Estas credenciales se cargan mediante `data.sql` en el arranque y están pensadas **solo para entornos de desarrollo/demo**. Cámbialas antes de cualquier despliegue productivo real.

---

## 🛠️ Arquitectura Técnica y Tecnologías

- **Backend:** Java 17, Spring Boot 3.3.0, Spring Web MVC, Spring Data JPA (Hibernate), Spring Security 6, Bean Validation, JJWT 0.11.5.
- **Base de Datos:** PostgreSQL 16 (Docker) / Neon Serverless Postgres en producción. Inicialización controlada vía `schema.sql` + `data.sql`.
- **Frontend:** HTML5, CSS3 (Modo Oscuro Empresarial), JavaScript Vanilla (ES6+, arquitectura por controllers/services/ui/utils), Chart.js 4.4.
- **Reportes:** Apache POI 5.2.5 (Excel) y OpenPDF 1.3.43 (PDF).
- **Correo:** Spring Mail sobre Gmail SMTP (TLS).
- **Documentación de API:** springdoc-openapi 2.5.0 (Swagger UI + OpenAPI 3).
- **Seguridad:** HTTPS SSL/TLS (Caddy + Let's Encrypt en producción), CORS configurable por variable de entorno, BCrypt, JWT Tokens, Google Identity Services (OAuth2 One Tap / botón "Sign in with Google").
- **Contenedorización:** Docker multi-stage build (`eclipse-temurin:17-jre-alpine`, usuario no root) + Docker Compose v2.
- **Despliegue:** Google Cloud Platform (Compute Engine + Caddy) y soporte nativo para Render (`render.yaml`).

---

## 📂 Estructura del Proyecto

```text
Proyecto-Spring-Boot-Tomas-Gonzalez-D1/
├── Dockerfile                     # Build multi-stage (deps → build → runtime JRE-alpine)
├── docker-compose.yml             # Orquesta PostgreSQL + app backend
├── render.yaml                    # Despliegue en Render (docker + Postgres gestionado)
├── DEPLOYMENT.md                  # Guía de despliegue en GCP + Caddy + Neon
├── .env.example                   # Plantilla de variables de entorno
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/proyecto/proyectoSpringBoot/
    │   │   ├── LogiTrackApplication.java
    │   │   ├── config/            # AsyncConfig, AuditorAwareConfig, SwaggerConfig
    │   │   ├── controller/        # 27 controladores REST (uno por módulo de negocio)
    │   │   ├── dto/
    │   │   │   ├── request/       # DTOs de entrada (Crear*, *Request)
    │   │   │   └── response/      # DTOs de salida (*Response)
    │   │   ├── event/             # AuditoriaEvent (auditoría asíncrona)
    │   │   ├── exception/         # GlobalExceptionHandler + excepciones de dominio
    │   │   ├── listener/          # AuditoriaEntityListener, AuditoriaEventListener, AuditoriaHelper
    │   │   ├── mapper/            # Conversión Entity ⇄ DTO por módulo
    │   │   ├── model/
    │   │   │   ├── entity/        # 25 entidades JPA (Bodega, Producto, Movimiento, Lote, etc.)
    │   │   │   └── enums/         # Estados y tipos de dominio (RolUsuario, TipoMovimiento, etc.)
    │   │   ├── repository/        # Spring Data JPA repositories
    │   │   ├── security/          # JwtUtil, JwtAuthFilter, SecurityConfig, UserDetailsServiceImpl
    │   │   ├── service/
    │   │   │   ├── interfaces/    # Contratos I*Service
    │   │   │   └── impl/          # Implementaciones (incluye EmailService/EmailTemplateBuilder)
    │   │   └── specification/     # JPA Specifications para filtros dinámicos (Producto, Movimiento, Auditoría)
    │   └── resources/
    │       ├── application.properties
    │       ├── schema.sql / data.sql
    │       └── static/            # Frontend integrado (SPA ligera, sin build step)
    │           ├── css/           # main.css, dashboard.css, components.css
    │           ├── js/            # app.js, config.js + controllers/services/ui/utils
    │           ├── views/         # dashboard, bodegas, productos, movimientos, lotes,
    │           │                  # conteos-ciclicos, ajustes, ordenes-compra, pedidos,
    │           │                  # picking, despachos, proveedores, clientes, usuarios,
    │           │                  # auditorias, reportes, zonas-series
    │           └── index.html     # Login (local + Google) y punto de entrada SPA
    └── test/                      # Pruebas con Spring Boot Test, Spring Security Test y H2
```

---

## 🔌 Catálogo de Endpoints de la API

Todos los endpoints están prefijados con `/api` y protegidos con JWT (salvo `/api/auth/**` y `/api/config/public`). El control de acceso por rol se aplica a nivel de método mediante `@PreAuthorize` según la matriz de roles descrita arriba.

| Módulo | Base Path | Controlador |
| :--- | :--- | :--- |
| Autenticación (login, registro, Google) | `/api/auth` | `AuthController` |
| Configuración pública / estadísticas | `/api/config` | `PublicConfigController` |
| Usuarios | `/api/usuarios` | `UsuarioController` |
| Bodegas | `/api/bodegas` | `BodegaController` |
| Zonas de bodega | `/api/zonas` | `ZonaBodegaController` |
| Ubicaciones físicas | `/api/ubicaciones` | `UbicacionBodegaController` |
| Tipos de ubicación | `/api/tipos-ubicacion` | `TipoUbicacionController` |
| Categorías | `/api/categorias` | `CategoriaController` |
| Unidades de medida | `/api/unidades-medida` | `UnidadMedidaController` |
| Productos | `/api/productos` | `ProductoController` |
| Números de serie | `/api/series` | `ProductoSerieController` |
| Lotes (incl. FEFO) | `/api/lotes` | `LoteController` |
| Movimientos de inventario | `/api/movimientos` | `MovimientoController` |
| Ajustes de inventario | `/api/ajustes` | `AjusteInventarioController` |
| Conteos cíclicos | `/api/conteos-ciclicos` | `ConteoCiclicoController` |
| Alertas de stock | `/api/alertas` | `AlertaStockController` |
| Notificaciones | `/api/notificaciones` | `NotificacionController` |
| Proveedores | `/api/proveedores` | `ProveedorController` |
| Órdenes de compra | `/api/ordenes-compra` | `OrdenCompraController` |
| Clientes | `/api/clientes` | `ClienteController` |
| Pedidos de clientes | `/api/pedidos` | `PedidoClienteController` |
| Tareas de picking | `/api/picking` | `TareaPickingController` |
| Guías de despacho | `/api/guias-despacho` | `GuiaDespachoController` |
| Transportadoras | `/api/transportadoras` | `TransportadoraController` |
| Auditoría del sistema | `/api/auditorias` | `AuditoriaController` |
| Reportes y exportación (Excel/PDF) | `/api/reportes` | `ReporteController` |

> El detalle exacto de cada endpoint (parámetros, esquemas de request/response) está disponible y navegable en **Swagger UI** una vez la aplicación está corriendo (ver sección siguiente).

---

## 🚀 Puesta en Marcha (Ejecución Local)

### Requisitos previos
- Java 17 (JDK)
- Maven 3.9+ (o usar el wrapper `./mvnw` incluido)
- Docker y Docker Compose (recomendado, para no instalar PostgreSQL manualmente)

### Opción 1 — Con Docker Compose (recomendada)

```bash
git clone https://github.com/TEstebanGQ/Proyecto-Spring-Boot-Tomas-Gonzalez-D1.git
cd Proyecto-Spring-Boot-Tomas-Gonzalez-D1

# Copia la plantilla de variables y ajusta lo necesario (JWT_SECRET, Google Client ID, correo)
cp .env.example .env

# Levanta PostgreSQL + la aplicación
docker compose up --build
```

La aplicación queda disponible en **http://localhost:8081**.

### Opción 2 — Ejecución local con Maven

```bash
git clone https://github.com/TEstebanGQ/Proyecto-Spring-Boot-Tomas-Gonzalez-D1.git
cd Proyecto-Spring-Boot-Tomas-Gonzalez-D1

# Levanta solo la base de datos con Docker
docker compose up -d postgres

# Exporta como mínimo el secreto JWT (obligatorio, sin valor por defecto seguro)
export JWT_SECRET=$(openssl rand -base64 64)

# Ejecuta la aplicación
./mvnw spring-boot:run
```

La app arranca en el puerto `8081` (configurable con la variable `PORT`), inicializa el esquema con `schema.sql` y precarga los usuarios de la tabla de credenciales con `data.sql`.

---

## ⚙️ Variables de Entorno

| Variable | Obligatoria | Por defecto | Descripción |
| :--- | :--- | :--- | :--- |
| `JWT_SECRET` | ✅ Sí | *(sin default seguro)* | Secreto para firmar los tokens JWT. Generar con `openssl rand -base64 64`. La app **no arranca** sin este valor en entornos serios. |
| `JWT_EXPIRATION` | No | `86400000` (24h) | Tiempo de expiración del token en milisegundos. |
| `GOOGLE_CLIENT_ID` | Recomendada | Client ID de desarrollo incluido | Client ID de Google Cloud Console para el botón "Acceder con Google". |
| `CORS_ALLOWED_ORIGINS` | No | `http://localhost:3000,http://localhost:8081,http://localhost:8080` | Orígenes permitidos para CORS, separados por coma. |
| `DB_URL` | No | `jdbc:postgresql://localhost:5435/logitrack_db` | URL de conexión JDBC a PostgreSQL. |
| `DB_USERNAME` | No | `postgres` | Usuario de la base de datos. |
| `DB_PASSWORD` | No | `postgres` | Contraseña de la base de datos. |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | No | `update` | Estrategia de Hibernate para el esquema (`update` en dev, `validate` recomendado en prod). |
| `SPRING_MAIL_HOST` / `SPRING_MAIL_PORT` | No | `smtp.gmail.com` / `587` | Servidor SMTP para el envío de correos. |
| `SPRING_MAIL_USERNAME` / `SPRING_MAIL_PASSWORD` | Para correo | *(vacío)* | Credenciales de la cuenta Gmail (usar contraseña de aplicación de 16 caracteres). |
| `APP_MAIL_ENABLED` | No | `true` | Habilita/deshabilita el envío real de correos. |
| `APP_MAIL_FROM` | No | `no-reply@logitrack.com` | Remitente mostrado en los correos salientes. |
| `PORT` | No | `8081` | Puerto en el que escucha el servidor embebido. |

Consulta `.env.example` para la plantilla completa lista para copiar.

---

## 📖 Documentación de la API (Swagger)

Con la aplicación corriendo, la documentación interactiva OpenAPI 3 está disponible en:

- **Swagger UI:** `http://localhost:8081/swagger-ui.html`
- **Especificación OpenAPI (JSON):** `http://localhost:8081/v3/api-docs`

---

## 🚀 Despliegue en la Nube & Protocolo de Pruebas

- **Guía Completa de Despliegue en la Nube (GCP, Caddy, Docker):**
  👉 **[Guía de Despliegue en la Nube (DEPLOYMENT.md)](DEPLOYMENT.md)**

- **Protocolo y Matriz de Pruebas de QA (Quality Assurance):**
  👉 **[Plan y Protocolo de Pruebas de QA (QA_TESTING_PLAN.md)](QA_TESTING_PLAN.md)**

- **Manual de Pruebas Interactivo End-to-End:**
  👉 **[Manual de Pruebas Interactivo (TESTING.md)](TESTING.md)**

---

## 📄 Licencia

Distribuido bajo licencia **MIT**. Proyecto académico desarrollado como parte del Técnico Laboral en Desarrollo de Software.