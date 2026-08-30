# LogiTrack S.A. - Sistema Backend de Gestión y Auditoría de Bodegas

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-brightgreen.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15%2B-blue.svg)
![JWT](https://img.shields.io/badge/Security-JWT-red.svg)
![Swagger](https://img.shields.io/badge/API_Docs-OpenAPI_3-green.svg)

## 📌 Descripción del Proyecto

**LogiTrack S.A.** es una solución backend centralizada construida con **Spring Boot** para la gestión integral de bodegas, control de inventario de productos, registro de movimientos (Entradas, Salidas y Transferencias) y auditoría automática de cambios en tiempo real.

El proyecto cuenta con protección mediante **Spring Security + JWT**, soporte completo para **PostgreSQL**, documentación interactiva **Swagger/OpenAPI 3** y un cliente web frontend modular en HTML5/CSS3/JavaScript Vanilla.

---

## 🛠️ Tecnologías Utilizadas

- **Lenguaje**: Java 17
- **Framework**: Spring Boot 3.3.0
- **Persistencia**: Spring Data JPA / Hibernate
- **Base de Datos**: PostgreSQL / Supabase
- **Seguridad**: Spring Security + io.jsonwebtoken (JWT 0.11.5)
- **Documentación**: Springdoc OpenAPI UI 2.5.0
- **Herramienta de Construcción**: Apache Maven
- **Frontend**: HTML5, CSS3 Vanilla (Dark Theme / Glassmorphism), JavaScript (ES6+ Modular)

---

## 🚀 Instalación y Ejecución

### 1. Requisitos Previos
- JDK 17 o superior instalado.
- Servidor PostgreSQL en ejecución (Local o Supabase) con una base de datos llamada `logitrack_db`.

### 2. Configuración de Base de Datos
Edita el archivo `src/main/resources/application.properties` si deseas ajustar la conexión a tu servidor PostgreSQL:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/logitrack_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver
```

### 3. Compilación y Ejecución Backend

#### Opción A: Con Docker Compose (Recomendado - Todo el Stack)
Si tienes Docker instalado, puedes iniciar todo el sistema (PostgreSQL + Spring Boot) con un solo comando sin necesidad de instalar Java ni PostgreSQL:

```bash
docker compose up --build
```

#### Opción B: Solo Base de Datos en Docker + Ejecución Local
Si deseas programar y depurar desde tu IDE o terminal:

1. Iniciar el contenedor de PostgreSQL:
   ```bash
   docker compose up -d postgres
   ```
2. Ejecutar la aplicación Spring Boot:
   ```bash
   ./mvnw spring-boot:run
   ```

#### Detener y Limpiar Contenedores
```bash
docker compose down
```
*(Para eliminar también los datos de la base de datos, agrega `-v`: `docker compose down -v`)*

---

## 📖 Documentación Swagger / OpenAPI 3

Una vez iniciada la aplicación, la documentación interactiva estará disponible en:
- **Swagger UI**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **API Docs (JSON)**: [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)

> **Nota para probar endpoints protegidos en Swagger**:
> 1. Realizar una petición a POST `/auth/login`.
> 2. Copiar el valor del campo `token`.
> 3. Hacer clic en el botón **Authorize** en la esquina superior de Swagger UI e ingresar `Bearer <tu_token>`.

---

## 🌐 Uso del Frontend Web Integrado

El cliente web modular está integrado directamente en la ubicación estándar de Spring Boot `src/main/resources/static/`.

Para utilizarlo:
1. Inicia la aplicación backend en Spring Boot (`./mvnw spring-boot:run`).
2. Accede en tu navegador a: [http://localhost:8081/](http://localhost:8081/)
3. Inicia sesión con las credenciales por defecto:
   - **Email**: `admin@logitrack.com`
   - **Contraseña**: `admin123`

---

## 🔑 Credenciales de Prueba por Defecto (`data.sql`)

| Rol | Usuario | Correo Electrónico | Contraseña | Permisos y Alcance |
| :--- | :--- | :--- | :--- | :--- |
| 🔑 **`ADMIN`** | Admin Sistema | `admin@logitrack.com` | `admin123` | Control Total + Gestión Usuarios + API Docs |
| 📊 **`GERENTE_LOGISTICA`** | Sofía Ramírez | `sofia@logitrack.com` | `empleado123` | Dashboard + Auditoría + Reportes Excel/PDF (Vista Ejecutiva) |
| 🕵️ **`SUPERVISOR`** | Laura Pérez | `laura@logitrack.com` | `empleado123` | Dashboard + Auditoría + Reportes + Edición/Eliminación Bodegas/Productos |
| 🛍️ **`JEFE_COMPRAS`** | Pedro Sánchez | `pedro@logitrack.com` | `empleado123` | Dashboard + Alta/Edición Productos + Movimientos (Entradas, Salidas, Transferencias) |
| 👤 **`EMPLEADO`** | Carlos González | `carlos@logitrack.com` | `empleado123` | Dashboard + Operación (Entradas/Salidas/Transferencias). Modo Lectura |

---

## 🔗 Catálogo de Endpoints Principal

### 1. Autenticación (`/auth`)
- `POST /auth/login`: Autenticación de usuarios y obtención de Token JWT.
- `POST /auth/register`: Registro de nuevos usuarios.

### 2. Bodegas (`/bodegas`)
- `GET /bodegas`: Listar todas las bodegas.
- `GET /bodegas/{id}`: Obtener bodega por ID.
- `POST /bodegas`: Crear nueva bodega (`ADMIN`).
- `PUT /bodegas/{id}`: Actualizar bodega existente (`ADMIN`).
- `DELETE /bodegas/{id}`: Desactivar/Eliminar bodega (`ADMIN`).

### 3. Productos (`/productos`)
- `GET /productos`: Listar productos.
- `GET /productos/bajo-stock`: Consultar productos con stock menor a 10 unidades.
- `POST /productos`: Crear producto.
- `PUT /productos/{id}`: Editar producto.
- `DELETE /productos/{id}`: Eliminar producto.

### 4. Movimientos (`/movimientos`)
- `GET /movimientos`: Consultar historial de movimientos (con filtros de fecha y tipo).
- `POST /movimientos`: Registrar movimiento (ENTRADA, SALIDA, TRANSFERENCIA).

### 5. Clientes y Proveedores (`/api/clientes`, `/api/proveedores`)
- `GET /api/clientes`, `POST /api/clientes`, `PUT /api/clientes/{id}`, `DELETE /api/clientes/{id}`
- `GET /api/proveedores`, `POST /api/proveedores`, `PUT /api/proveedores/{id}`, `DELETE /api/proveedores/{id}`

### 6. Órdenes de Compra (`/api/ordenes-compra`)
- `POST /api/ordenes-compra`: Emitir orden de compra a un proveedor en estado PENDIENTE.
- `PUT /api/ordenes-compra/{id}/aprobar`: Aprobar formalmente una orden de compra.
- `PUT /api/ordenes-compra/{id}/recibir`: Recepción física en almacén que incrementa automáticamente el inventario.
- `PUT /api/ordenes-compra/{id}/cancelar`: Anulación justificada de pedido.

### 7. Ajustes de Inventario y Control de Mermas (`/api/ajustes`)
- `POST /api/ajustes`: Registrar ajuste por Merma, Avería, Vencimiento o Conteo Físico.
- `GET /api/ajustes`: Listar histórico de ajustes y descuadres con justificación.

### 8. Lotes y Trazabilidad FEFO (`/api/lotes`)
- `POST /api/lotes`: Registrar lote de producto con fechas de fabricación y caducidad.
- `GET /api/lotes/fefo`: Listar lotes ordenados por vencimiento para rotación First Expired, First Out.
- `GET /api/lotes/proximos-vencer`: Alerta de lotes con caducidad cercana.

### 9. Ubicaciones Físicas en Bodegas (`/api/ubicaciones`)
- `POST /api/ubicaciones`: Registrar pasillos, racks y niveles en almacenes.
- `GET /api/ubicaciones/bodega/{bodegaId}`: Consultar mapa físico de bodega.

### 10. Auditorías (`/api/auditorias`)
- `GET /api/auditorias`: Consultar registros automáticos de auditoría (filtrar por usuario u operación).

### 11. Reportes y Clasificación ABC (`/api/reportes`)
- `GET /api/reportes/resumen`: Obtener reporte general de existencias y productos más movidos.
- `GET /api/reportes/clasificacion-abc`: Matriz de clasificación ABC según valorización del inventario.
- `GET /api/reportes/exportar/excel`, `GET /api/reportes/exportar/pdf`: Descarga de reportes ejecutivos.

---

## 📧 Configuración de Correo Electrónico (Gmail SMTP)

LogiTrack envía un correo electrónico de bienvenida profesional en formato HTML adaptado al rol del usuario (`ADMIN`, `SUPERVISOR`, `GERENTE_LOGISTICA`, `JEFE_COMPRAS`, `EMPLEADO`) al completar su registro.

### Pasos para configurar Gmail con Contraseña de Aplicación:

1. Ve a la [Seguridad de tu Cuenta de Google](https://myaccount.google.com/security) y activa la **Verificación en 2 pasos**.
2. Ingresa a [Contraseñas de Aplicación](https://myaccount.google.com/apppasswords).
3. En el nombre de la app escribe `LogiTrack` y haz clic en **Crear**.
4. Copia la contraseña de 16 caracteres generada (ej: `xxxx xxxx xxxx xxxx`).
5. Configura las variables en tu archivo `.env` o en las variables de entorno:

```env
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=tu_cuenta@gmail.com
SPRING_MAIL_PASSWORD=tu_contraseña_de_aplicación_16_caracteres
APP_MAIL_ENABLED=true
APP_MAIL_FROM=no-reply@logitrack.com
```

> **Nota:** Si no se configuran las variables de correo en desarrollo, el sistema funcionará normalmente registrando los usuarios sin interrumpir el flujo.

---

## 🧪 Pruebas Automatizadas
El sistema incluye una suite de pruebas unitarias y de integración completas:
```bash
./mvnw test
```

