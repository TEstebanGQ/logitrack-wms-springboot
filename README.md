---
title: LogiTrack WMS Enterprise
emoji: 📦
colorFrom: blue
colorTo: indigo
sdk: docker
app_port: 8081
pinned: false
---

# 📦 LogiTrack S.A. — WMS & Sistema de Gestión de Bodegas e Inventario

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

**LogiTrack S.A.** es un sistema de gestión de almacenes e inventarios de clase empresarial (**Warehouse Management System - WMS**). Diseñado para garantizar **trazabilidad del 100%** en tiempo real de todos los productos, entradas, salidas, movimientos internos y auditorías físicas de mercancía.

---

## 🏢 Lógica de Negocio y Dominio Logístico

LogiTrack S.A. resuelve la complejidad logística de empresas con múltiples bodegas, garantizando el control riguroso de mercancía mediante los siguientes ejes operacionales:

### 1. 📦 Gestión de Inventarios y Bodegas Multisitio
- **Multi-Bodega & Ubicaciones:** Control de stock distribuido por bodegas físicas, pasillos, estantes, racks y gavetas.
- **Trazabilidad de Movimientos:** Cada ingreso (**ENTRADA**), egreso (**SALIDA**) o traslado (**TRANSFERENCIA**) queda registrado de forma inmutable con fecha, hora, usuario operador, cantidad y bodega de origen/destino.
- **Números de Serie Físicos:** Trazabilidad unitaria para productos tecnológicos o de alto valor.

### 2. ⏳ Control de Lotes, Vencimientos y Cuarentena
- **Gestión por Lotes:** Registro de fecha de fabricación y fecha de caducidad para insumos perecederos o farmacéuticos.
- **Estados de Lote:** `DISPONIBLE`, `CUARENTENA` (inspección de calidad), `AGOTADO` y `VENCIDO`.
- **Alertas de Caducidad:** Notificación automática cuando un lote se aproxima a su fecha de vencimiento.

### 3. 🔍 Conteos Cíclicos y Auditoría Físico vs. Sistema
- **Auditorías de Stock:** Creación de ordenes de conteo físico parciales o totales en bodega.
- **Conciliación de Diferencias:** Comparación automática entre el stock registrado en sistema y el conteo físico en estantería.
- **Ajustes Justificados:** Registro de sobrantes o faltantes con justificación de auditoría.

### 4. 📈 Clasificación ABC & Valorización de Inventario
- **Análisis de Pareto (80-15-5):** Clasificación automática de productos según su valor e impacto financiero:
  - **Tipo A (80% valor):** Productos críticos de alta valorización y control estricto.
  - **Tipo B (15% valor):** Productos de rotación y valor intermedio.
  - **Tipo C (5% valor):** Insumos de menor impacto monetario.

### 5. 🚚 Pedidos, Picking y Guías de Despacho
- **Flujo Comercial Integrado:** Creación de pedidos de cliente, asignación de tareas de recolección (**Picking**) paso a paso en bodega y generación de **Guías de Despacho** oficiales vinculadas a empresas de transporte.

### 6. 📧 Servicio de Notificaciones Automáticas por Correo (Gmail SMTP)
- **Bienvenida Adaptada:** Correo formateado según el rol asignado al registrarse.
- **Alertas de Stock Bajo:** Notificación inmediata a administradores cuando un producto cae por debajo de su umbral mínimo.
- **Notificación de Cambio de Rol:** Correo automático enviado al usuario cuando un Administrador modifica sus permisos.
- **Reporte Ejecutivo Diario bajo Demanda:** Generación y envío por correo a todos los directivos (`SUPER_ADMIN`, `ADMIN`, `GERENTE_LOGISTICA`) con el resumen de movimientos del día.

### 7. 🔑 Autenticación Dual (JWT Local + OAuth2 Google Sign-In)
- **Inicio de Sesión Local:** Autenticación mediante credenciales protegidas con `BCrypt` y tokens `JWT` de 256 bits.
- **Acceder con Google:** Autenticación mediante Google Identity Services. Si es un usuario nuevo, le solicita definir obligatoriamente su contraseña personal y lo registra automáticamente con el rol **`EMPLEADO`**.

---

## 👑 Matriz de Roles y Jerarquía de Seguridad

La plataforma implementa **Jerarquía de Roles de Spring Security 6** (`RoleHierarchy`), garantizando que los roles superiores hereden todos los permisos de los roles inferiores:

```text
ROLE_SUPER_ADMIN > ROLE_ADMIN > ROLE_GERENTE_LOGISTICA > ROLE_SUPERVISOR > ROLE_JEFE_COMPRAS > ROLE_EMPLEADO
```

| Rol | Atribuciones y Alcance en el Sistema |
| :--- | :--- |
| **`SUPER_ADMIN`** | **Super Administrador Global.** Acceso total sin restricciones a toda la plataforma, reportes directivos, auditoría global y gestión de otros administradores. |
| **`ADMIN`** | **Administrador del Sistema.** Creación de usuarios, gestión de bodegas, parametrización de catálogo y configuración general. |
| **`GERENTE_LOGISTICA`** | **Gerencia Logística.** Emisión de guías de despacho, reportes ejecutivos, análisis ABC, notificaciones directivas y supervisión de pedidos. |
| **`SUPERVISOR`** | **Supervisión de Operaciones.** Planificación de conteos cíclicos, conciliación de inventario, aprobación de lotes y control de calidad. |
| **`JEFE_COMPRAS`** | **Jefatura de Abastecimiento.** Creación y aprobación de órdenes de compra a proveedores y recepción de mercancía. |
| **`EMPLEADO`** | **Operador de Bodega.** Registro de entradas, salidas, transferencias físicas entre pasillos y ejecución de tareas de picking. |

---

## 🔑 Credenciales Predeterminadas del Sistema

Al iniciar por primera vez, el sistema precarga las siguientes cuentas de prueba:

| Rol | Correo Electrónico | Contraseña |
| :--- | :--- | :--- |
| **Super Administrador** | `tomasestebangonzalezquintero@gmail.com` | `admin123` *(o botón **Acceder con Google**)* |
| **Administrador** | `admin@logitrack.com` | `admin123` |
| **Supervisor** | `laura@logitrack.com` | `empleado123` |
| **Gerente Logística** | `sofia@logitrack.com` | `empleado123` |
| **Jefe de Compras** | `pedro@logitrack.com` | `empleado123` |
| **Empleado Operativo** | `carlos@logitrack.com` | `empleado123` |

---

## 🛠️ Arquitectura Técnica

- **Backend:** Java 17, Spring Boot 3.3.0, Spring Data JPA, Spring Security 6, JJWT 0.11.5.
- **Base de Datos:** PostgreSQL 16 / Neon Serverless Postgres.
- **Frontend:** HTML5, CSS3 modular (Modo Oscuro Empresarial), JavaScript Vanilla (ES6+), Chart.js 4.4.
- **Seguridad:** HTTPS SSL/TLS (Caddy + Let's Encrypt), CORS restringido por patrones, BCrypt, JWT Tokens.
- **Contenedorización:** Docker Multi-stage build (`eclipse-temurin:17-jre-alpine`) + Docker Compose v2.

---

## 🚀 Despliegue en la Nube

Para consultar la guía detallada paso a paso sobre la infraestructura en **Google Cloud Platform (GCP)**, **Caddy Reverse Proxy** y la configuración del entorno, consulta el archivo:

👉 **[Guía Completa de Despliegue en la Nube (DEPLOYMENT.md)](DEPLOYMENT.md)**
