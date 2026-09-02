# 🧪 Guía Interactiva de Pruebas Extremo a Extremo (End-to-End) — LogiTrack S.A.

Esta guía contiene los datos exactos, pasos secuenciales y credenciales para probar **absolutamente el 100% de las funcionalidades de la plataforma LogiTrack S.A.**.

---

## 🔑 Credenciales de Acceso por Rol (Inicio de Sesión Local)

| Rol a Probar | Correo Electrónico | Contraseña | Alcance de la Prueba |
| :--- | :--- | :--- | :--- |
| **Super Administrador** | `tomasestebangonzalezquintero@gmail.com` | `admin123` *(o Google)* | Acceso total global, auditorías, envío de reportes directivos. |
| **Administrador** | `admin@logitrack.com` | `admin123` | Crear usuarios, bodegas, productos, ubicaciones y configuración. |
| **Gerente de Logística** | `sofia@logitrack.com` | `empleado123` | Emitir guías de despacho, enviar reporte diario y ver analítica. |
| **Supervisor** | `laura@logitrack.com` | `empleado123` | Conteos cíclicos, conciliar inventario y cambiar estado a lotes. |
| **Jefe de Compras** | `pedro@logitrack.com` | `empleado123` | Emitir órdenes de compra, gestionar proveedores y recepciones. |
| **Empleado Operativo** | `carlos@logitrack.com` | `empleado123` | Entradas/salidas, recibir mercancía, picking y despachos. |

---

## 🧪 Pruebas Módulo por Módulo (Datos de Formulario Listos para Copiar y Pegar)

---

### 1. 🏬 Prueba de Gestión de Bodegas y Ubicaciones Físicas
- **Usuario para ingresar:** `admin@logitrack.com` (`admin123`)
- **Sección:** *Bodegas* ➔ Botón **`[ + Nueva Bodega ]`**

#### 📋 Datos a Registrar:
- **Nombre:** `Bodega Eje Cafetero - Armenia`
- **Ubicación:** `Armenia, Quindío - Zona Franca Parque Industrial`
- **Capacidad Máxima:** `4500`
- **Encargado:** `Laura Pérez`
- **Estado:** `Activo`

#### 📍 Crear Ubicación Física en la Bodega:
- **Sección:** *Zonas & Ubicaciones* ➔ **`[ + Nueva Ubicación ]`**
- **Bodega:** `Bodega Central Bogotá`
- **Código Ubicación:** `B1-PAS4-RACK-D1`
- **Pasillo:** `Pasillo 4` | **Estante:** `Rack D` | **Nivel:** `Nivel 1 (Bajo)`
- **Capacidad Máxima:** `300`

---

### 2. 📦 Prueba de Catálogo de Productos y Números de Serie
- **Usuario para ingresar:** `admin@logitrack.com` (`admin123`) o `laura@logitrack.com` (`empleado123`)
- **Sección:** *Productos* ➔ Botón **`[ + Registrar Producto ]`**

#### 📋 Datos a Registrar:
- **Nombre del Producto:** `Laptop Lenovo ThinkPad T14 Gen 4`
- **Categoría:** `Electrónica & TI`
- **Precio Unitario:** `3800000`
- **Stock Mínimo:** `10`
- **Descripción:** `Laptop empresarial Core i7 13th Gen, 16GB RAM, 512GB SSD`

#### 🏷️ Registrar Número de Serie Unitario:
- **Sección:** *Zonas & Series* ➔ Pestaña *Números de Serie* ➔ **`[ + Nuevo Número de Serie ]`**
- **Número de Serie:** `SN-THINK-2026-999`
- **Producto:** `Laptop Lenovo ThinkPad T14 Gen 4`
- **Bodega:** `Bodega Central Bogotá`
- **Estado:** `EN_STOCK`

---

### 3. 🏢 Prueba de Terceros: Proveedores, Clientes y Transportadoras
- **Usuario para ingresar:** `pedro@logitrack.com` (Jefe Compras) o `sofia@logitrack.com` (Gerente)
- **Sección:** *Proveedores / Clientes / Transportadoras*

#### 🏭 Datos para Nuevo Proveedor:
- **Nombre:** `Distribuidora de Tecnología del Valle S.A.S.`
- **NIT / RUC:** `900555444-1`
- **Teléfono:** `+57 602 555 9999`
- **Email:** `ventas@techvalle.co`
- **Dirección:** `Avenida 6N # 28-15, Cali`

#### 🏢 Datos para Nuevo Cliente:
- **Nombre:** `Soluciones Tecnológicas del Norte S.A.`
- **NIT / RUC:** `800777666-2`
- **Teléfono:** `+57 605 444 8888`
- **Email:** `compras@tecnorte.co`
- **Dirección:** `Calle 79 # 53-40, Barranquilla`

#### 🚛 Datos para Nueva Transportadora:
- **Nombre:** `Envíos & Carga Nacional Express S.A.`
- **NIT / RUC:** `901888999-3`
- **Teléfono:** `+57 601 888 7777`
- **Email:** `contacto@cargaexpress.co`
- **Servicio:** `TERRESTRE`

---

### 4. 🛒 Prueba del Flujo de Compras y Recepción de Mercancía
- **Paso A (Emitir Orden):** Ingresa con `pedro@logitrack.com` (Jefe de Compras).
  - Sección *Órdenes de Compra* ➔ **`[ + Nueva Orden de Compra ]`**.
  - **Proveedor:** `TechSupplies Colombia S.A.S.`
  - **Bodega Destino:** `Bodega Central Bogotá`
  - **Producto:** `Laptop Dell XPS 15 Pro` | **Cantidad:** `5` | **Precio:** `4500000`
- **Paso B (Aprobar Orden):** Haz clic en **`[ Aprobar ]`**.
- **Paso C (Recibir Mercancía):** Ingresa con `carlos@logitrack.com` (Empleado Operativo).
  - Ve a *Órdenes de Compra* ➔ Ubica la orden aprobada y presiona **`[ Recibir Mercancía ]`**.
  - *Resultado:* El sistema incrementará el inventario en la bodega seleccionada y registrará la entrada.

---

### 5. 🚛 Prueba del Flujo Comercial: Pedidos, Picking y Despachos
- **Paso A (Registrar Pedido):** Ingresa con `sofia@logitrack.com` (Gerente Logística).
  - Sección *Pedidos* ➔ **`[ + Nuevo Pedido ]`**.
  - **Cliente:** `Soluciones Digitales ABC S.A.S.` | **Bodega Origen:** `Bodega Central Bogotá`
  - **Producto:** `Teclado Mecánico RGB` | **Cantidad:** `3`
- **Paso B (Ejecutar Picking):** Ingresa con `carlos@logitrack.com` (Empleado).
  - Sección *Picking* ➔ Selecciona la tarea asignada y haz clic en **`[ Recolectar ]`**.
- **Paso C (Despachar y Generar Guía):**
  - Ve a *Despachos* ➔ Presiona **`[ + Emitir Guía de Despacho ]`**.
  - Asigna la Transportadora `LogiExpress Colombia S.A.` y el Conductor `Javier Peña`.

---

### 6. 🏷️ Prueba de Control de Lotes y Vencimientos (FEFO)
- **Usuario para ingresar:** `laura@logitrack.com` (Supervisor)
- **Sección:** *Lotes y Vencimientos* ➔ **`[ Registrar Lote ]`**

#### 📋 Datos a Registrar:
- **Código Lote:** `LOT-THINK-2026-01`
- **Producto:** `Laptop Lenovo ThinkPad T14 Gen 4`
- **Bodega:** `Bodega Central Bogotá`
- **Cantidad Inicial / Stock:** `20`
- **Fecha Fabricación:** *(Fecha de hoy)*
- **Fecha Vencimiento:** *(Fecha dentro de 15 días para probar la insignia PRÓXIMO A VENCER)*

#### 🔄 Cambiar Estado de Lote:
- Haz clic en **`[ ESTADO ]`** sobre cualquier lote para cambiar su estado entre `DISPONIBLE`, `CUARENTENA` o `VENCIDO`.

---

### 7. 📋 Prueba de Auditoría y Conteos Cíclicos
- **Usuario para ingresar:** `laura@logitrack.com` (Supervisor)
- **Sección:** *Conteos Cíclicos* ➔ **`[ + Programar Conteo Cíclico ]`**

#### 📋 Pasos de la Prueba:
1. Programar conteo en `Bodega Central Bogotá` para el pasillo de laptops.
2. Hacer clic en **`[ Registrar Conteo ]`** e ingresar el valor contado (ej: si el sistema dice `25`, ingresar `24`).
3. Hacer clic en **`[ Conciliar Inventario ]`**: El sistema calculará la diferencia de `-1` y generará automáticamente un registro en **Ajustes de Inventario** por merma/faltante.

---

### 8. 🔔 Prueba de Alertas y Envió de Reporte Diario por Correo
- **Usuario para ingresar:** `tomasestebangonzalezquintero@gmail.com` (Super Admin) o `sofia@logitrack.com` (Gerente)
- **Sección:** *Reportes*

#### 📧 Pasos para Enviar el Reporte Diario:
1. En la pestaña *Resumen General*, ubica la tarjeta superior de exportación.
2. Haz clic en el botón destacado: **`[ 📧 Enviar Reporte Diario - Correo a Admins / Gerentes ]`**.
3. Confirma el cuadro modal: El sistema compilará la actividad del día y enviará el correo HTML a los directivos.
4. Revisa la campana de notificaciones en el header superior para verificar las alertas no leídas.

---

### 9. 🔑 Prueba de Registro con Google OAuth2
1. En la pantalla de inicio de sesión (`/auth`), haz clic en el botón azul **`[ G Acceder con Google ]`**.
2. Selecciona tu cuenta de Google.
3. Si es tu primera vez, se abrirá automáticamente el modal exigiendo ingresar una **Contraseña Personal Obligatoria**.
4. Al completar la contraseña, el sistema te registrará automáticamente con el rol **`EMPLEADO`** y te otorgará acceso a la plataforma.
