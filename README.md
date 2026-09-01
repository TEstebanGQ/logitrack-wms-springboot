---
title: LogiTrack WMS Enterprise
emoji: 📦
colorFrom: blue
colorTo: indigo
sdk: docker
app_port: 8081
pinned: false
---

# 📦 LogiTrack S.A. — Sistema WMS & Gestión Completa de Bodegas e Inventario

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

**LogiTrack S.A.** es un sistema de gestión de almacenes e inventarios de clase empresarial (**Warehouse Management System - WMS**). Diseñado para garantizar **trazabilidad del 100% en tiempo real** de todas las operaciones logísticas: recepción de mercancía, almacenamiento multisitio, traslados entre bodegas, control de lotes, picking, despachos, órdenes de compra y auditorías físicas de stock.

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
- **Directorio de Proveedores:** Registro de contactos, RUTC/NIT y condiciones comerciales.
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

### 10. 🔑 Autenticación Dual & Seguridad
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

---

## 🛠️ Arquitectura Técnica y Tecnologías

- **Backend:** Java 17, Spring Boot 3.3.0, Spring Data JPA, Spring Security 6, JJWT 0.11.5.
- **Base de Datos:** PostgreSQL 16 / Neon Serverless Postgres.
- **Frontend:** HTML5, CSS3 (Modo Oscuro Empresarial), JavaScript Vanilla (ES6+), Chart.js 4.4.
- **Seguridad:** HTTPS SSL/TLS (Caddy + Let's Encrypt), CORS por patrones, BCrypt, JWT Tokens.
- **Contenedorización:** Docker Multi-stage build (`eclipse-temurin:17-jre-alpine`) + Docker Compose v2.

---

## 🚀 Despliegue en la Nube (Google Cloud Platform)

Para consultar la guía detallada paso a paso sobre la infraestructura en **Google Cloud Platform (GCP)**, **Caddy Reverse Proxy** y la configuración del servidor, consulta:

👉 **[Guía Completa de Despliegue en la Nube (DEPLOYMENT.md)](DEPLOYMENT.md)**
