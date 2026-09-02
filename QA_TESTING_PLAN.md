# 🧪 Plan y Protocolo de Pruebas de Aseguramiento de Calidad (QA Testing Plan) — LogiTrack S.A.

Este documento contiene la matriz oficial de **Casos de Prueba de QA (Quality Assurance)** para la validación funcional, de seguridad, de trazabilidad logística y de desempeño del sistema de gestión de almacenes **LogiTrack S.A. WMS**.

---

## 📊 Matriz de Resumen de Cobertura de QA

| ID Caso de Prueba | Módulo Funcional Evaluado | Rol Ejecutor | Tipo de Prueba | Estado de Ejecución |
| :--- | :--- | :--- | :--- | :---: |
| **`QA-TC-001`** | Autenticación Dual (JWT & Google OAuth2) | Público / `EMPLEADO` | Seguridad / Auth | ✅ **PASSED** |
| **`QA-TC-002`** | Matriz de Permisos RBAC & Botones por Rol | `SUPERVISOR` / `ADMIN` | Seguridad / UI | ✅ **PASSED** |
| **`QA-TC-003`** | Bodegas Multisitio & Ubicaciones Físicas | `ADMIN` / `SUPER_ADMIN` | Lógica de Negocio | ✅ **PASSED** |
| **`QA-TC-004`** | Catálogo de Productos & Números de Serie | `ADMIN` / `SUPERVISOR` | Trazabilidad | ✅ **PASSED** |
| **`QA-TC-005`** | Compras & Recepción de Mercancía | `JEFE_COMPRAS` / `EMPLEADO` | Flujo de Entrada | ✅ **PASSED** |
| **`QA-TC-006`** | Pedidos de Cliente, Picking & Despachos | `GERENTE` / `EMPLEADO` | Flujo de Salida | ✅ **PASSED** |
| **`QA-TC-007`** | Control de Lotes & Caducidad FEFO/FIFO | `SUPERVISOR` / `ADMIN` | Rotación Stock | ✅ **PASSED** |
| **`QA-TC-008`** | Conteos Cíclicos & Ajustes de Inventario | `SUPERVISOR` / `ADMIN` | Auditoría Físico | ✅ **PASSED** |
| **`QA-TC-009`** | Alertas de Bajo Stock & Notificaciones en Vivo | Todos los usuarios | Alertas / UI | ✅ **PASSED** |
| **`QA-TC-010`** | Envió de Reporte Diario por Correo (Gmail SMTP)| `SUPER_ADMIN` / `GERENTE` | Integración Email | ✅ **PASSED** |

---

## 🛠️ Detalle Paso a Paso de Casos de Prueba (QA Execution Manual)

---

### 🔑 Caso de Prueba 1: `QA-TC-001` — Registro & Autenticación Dual (JWT & Google OAuth2)
- **Objetivo:** Verificar que un usuario nuevo registrado mediante Google Sign-In sea asignado obligatoriamente con el rol **`EMPLEADO`** y se le solicite contraseña personal.
- **Precondiciones:** Estar en la pantalla de inicio de sesión (`/#/auth`).

#### 📋 Pasos de Ejecución:
1. Hacer clic en el botón **`[ G Acceder con Google ]`**.
2. Seleccionar una cuenta de Google activa.
3. Al detectar que la cuenta no existe en base de datos, el sistema despliega el modal `modal-google-complete`.
4. Ingresar una contraseña personal (ej: `Empleado2026*`).
5. Confirmar el formulario de registro.

#### ✅ Criterios de Aceptación & Resultado Esperado:
- El backend registra al usuario con `rol = EMPLEADO` y contraseña encriptada con `BCrypt`.
- Se genera el token JWT y el sistema redirige al usuario a la vista principal.
- Se envía el correo electrónico de bienvenida correspondiente al rol asignado.

---

### 🛡️ Caso de Prueba 2: `QA-TC-002` — Control de Acceso Basado en Roles (RBAC)
- **Objetivo:** Validar que el rol `SUPERVISOR` no pueda visualizar ni ejecutar acciones de eliminación o edición en el módulo de Bodegas.
- **Precondiciones:** Iniciar sesión con la cuenta de Supervisor (`laura@logitrack.com` / `empleado123`).

#### 📋 Pasos de Ejecución:
1. Iniciar sesión y navegar al **Dashboard**.
2. Inspeccionar las tarjetas de resumen de Bodegas.
3. Navegar a la sección **Bodegas**.

#### ✅ Criterios de Aceptación & Resultado Esperado:
- En las tarjetas del Dashboard **NO aparecen** los botones **Editar** ni **Eliminar**.
- En la tabla de la vista Bodegas **NO aparecen** los botones **Editar** ni **Eliminar**.
- Si se intenta consumir la API directamente (`DELETE /api/bodegas/1`), el backend retorna un código **`HTTP 403 Forbidden`**.

---

### 🏬 Caso de Prueba 3: `QA-TC-003` — Bodegas Multisitio & Ubicaciones Físicas
- **Objetivo:** Verificar la creación de bodegas y la asignación de ubicaciones físicas en estantería.
- **Precondiciones:** Iniciar sesión como Administrador (`admin@logitrack.com` / `admin123`).

#### 📋 Pasos de Ejecución:
1. Ir al menú **Bodegas** ➔ Botón **`[ + Nueva Bodega ]`**.
2. Ingresar:
   - **Nombre:** `Bodega Eje Cafetero - Armenia`
   - **Ubicación:** `Armenia, Quindío - Zona Franca Parque Industrial`
   - **Capacidad:** `4500`
   - **Encargado:** `Laura Pérez`
3. Guardar el registro.
4. Ir al menú **Zonas & Ubicaciones** ➔ **`[ + Nueva Ubicación ]`**.
5. Asignar Código `B1-PAS4-RACK-D1`, Pasillo `Pasillo 4`, Estante `Rack D`, Nivel `Nivel 1 (Bajo)`.

#### ✅ Criterios de Aceptación & Resultado Esperado:
- La bodega aparece listada en la tabla y en los selectores dinámicos de los demás módulos.
- La ubicación queda vinculada correctamente y disponible para almacenamiento de productos.

---

### 📦 Caso de Prueba 4: `QA-TC-004` — Productos & Números de Serie
- **Objetivo:** Garantizar la creación de productos en catálogo y el registro de trazabilidad por número de serie.
- **Precondiciones:** Iniciar sesión como `ADMIN` o `SUPERVISOR`.

#### 📋 Pasos de Ejecución:
1. Navegar a **Productos** ➔ **`[ + Registrar Producto ]`**.
2. Ingresar:
   - **Nombre:** `Laptop Lenovo ThinkPad T14 Gen 4`
   - **Categoría:** `Electrónica & TI`
   - **Precio:** `3800000` | **Stock Mínimo:** `10`
3. Navegar a **Zonas & Series** ➔ Pestaña *Números de Serie* ➔ **`[ + Nuevo Número de Serie ]`**.
4. Ingresar `SN-THINK-2026-999` para el producto registrado en `Bodega Central Bogotá` con estado `EN_STOCK`.

#### ✅ Criterios de Aceptación & Resultado Esperado:
- El producto queda grabado con su SKU y precio formateado en COP ($3,800,000).
- El número de serie queda asociado y trazado individualmente en el inventario.

---

### 🛒 Caso de Prueba 5: `QA-TC-005` — Flujo de Compras y Recepción de Mercancía
- **Objetivo:** Validar la emisión, aprobación e ingreso automático de mercancía a almacén.
- **Precondiciones:** Cuentas `pedro@logitrack.com` (`JEFE_COMPRAS`) y `carlos@logitrack.com` (`EMPLEADO`).

#### 📋 Pasos de Ejecución:
1. Con `pedro@logitrack.com`, ir a **Órdenes de Compra** ➔ **`[ + Nueva Orden de Compra ]`**.
2. Seleccionar Proveedor `TechSupplies Colombia S.A.S.` y agregar 5 Laptops Dell XPS 15 Pro.
3. Presionar el botón **`[ Aprobar ]`**.
4. Iniciar sesión con `carlos@logitrack.com` (Empleado).
5. Ir a **Órdenes de Compra** ➔ Presionar **`[ Recibir Mercancía ]`** en la orden aprobada.

#### ✅ Criterios de Aceptación & Resultado Esperado:
- La orden de compra cambia su estado a `RECIBIDA`.
- El saldo de la bodega destino se incrementa automáticamente en 5 unidades.
- Se genera un registro de movimiento tipo `ENTRADA` y una entrada en la bitácora de **Auditoría**.

---

### 🚛 Caso de Prueba 6: `QA-TC-006` — Pedidos, Picking & Guías de Despacho
- **Objetivo:** Verificar la ruta de salida de mercancía desde el pedido comercial hasta la entrega física.
- **Precondiciones:** Cuentas `sofia@logitrack.com` (`GERENTE_LOGISTICA`) y `carlos@logitrack.com` (`EMPLEADO`).

#### 📋 Pasos de Ejecución:
1. Con `sofia@logitrack.com`, ir a **Pedidos** ➔ **`[ + Nuevo Pedido ]`** para el cliente `Soluciones Digitales ABC S.A.S.`.
2. Con `carlos@logitrack.com`, ir a **Picking** ➔ Hacer clic en **`[ Recolectar ]`** en la tarea asignada.
3. Ir a **Despachos** ➔ **`[ + Emitir Guía de Despacho ]`** asignando la transportadora `LogiExpress Colombia S.A.`.

#### ✅ Criterios de Aceptación & Resultado Esperado:
- El pedido cambia a estado `DESPACHADO`.
- Se genera la Guía de Despacho con número de guía oficial y estado `EN_TRANSITO`.
- El stock de la bodega de origen disminuye en la cantidad despachada.

---

### 🏷️ Caso de Prueba 7: `QA-TC-007` — Control de Lotes y Caducidad (FEFO/FIFO)
- **Objetivo:** Validar el control por lotes y las alertas de vencimiento de productos.
- **Precondiciones:** Iniciar sesión con `laura@logitrack.com` (`SUPERVISOR`).

#### 📋 Pasos de Ejecución:
1. Ir a **Lotes y Vencimientos** ➔ **`[ Registrar Lote ]`**.
2. Código Lote: `LOT-THINK-2026-01`, Producto: `Laptop Lenovo ThinkPad T14`, Stock: `20`.
3. Asignar fecha de vencimiento a 15 días desde hoy.
4. Hacer clic en **`[ ESTADO ]`** y cambiar entre `DISPONIBLE` y `CUARENTENA`.

#### ✅ Criterios de Aceptación & Resultado Esperado:
- Los lotes cercanos a vencer muestran la insignia amarilla **`PRÓXIMO A VENCER`**.
- Los lotes en `CUARENTENA` quedan bloqueados para despachos comerciales.

---

### 📋 Caso de Prueba 8: `QA-TC-008` — Conteos Cíclicos & Conciliación Físico vs. Sistema
- **Objetivo:** Comprobar la auditoría física de estantería y la generación de ajustes por faltantes o mermas.
- **Precondiciones:** Iniciar sesión como `SUPERVISOR`.

#### 📋 Pasos de Ejecución:
1. Ir a **Conteos Cíclicos** ➔ **`[ + Programar Conteo Cíclico ]`** en `Bodega Central Bogotá`.
2. Presionar **`[ Registrar Conteo ]`** e ingresar un valor físico inferior al registrado en sistema.
3. Presionar **`[ Conciliar Inventario ]`**.

#### ✅ Criterios de Aceptación & Resultado Esperado:
- El sistema calcula automáticamente la diferencia negativa (faltante).
- Se genera un registro automático en **Ajustes de Inventario** ajustando el saldo en bodega y registrando la justificación de auditoría.

---

### 🔔 Caso de Prueba 9: `QA-TC-009` — Alertas de Stock Bajo & Notificaciones en Vivo
- **Objetivo:** Comprobar que la disminución de stock enciende la campana de notificaciones en el header.
- **Precondiciones:** Iniciar sesión con cualquier usuario.

#### 📋 Pasos de Ejecución:
1. Disminuir el stock de un producto por debajo de su `stockMinimo`.
2. Observar la barra de navegación superior (Header).
3. Hacer clic en el icono de la campana.

#### ✅ Criterios de Aceptación & Resultado Esperado:
- La insignia roja de la campana muestra el número de notificaciones no leídas (`data.contador`).
- Al desplegar la campana, se visualiza la tarjeta de alerta con icono rojo y mensaje explicativo.
- Al presionar **`[ Marcar leída ]`**, el contador disminuye en tiempo real.

---

### 📧 Caso de Prueba 10: `QA-TC-010` — Envío de Reporte Ejecutivo Diario por Correo
- **Objetivo:** Verificar la generación y despacho del informe consolidado HTML a los directivos vía Gmail SMTP.
- **Precondiciones:** Iniciar sesión con `tomasestebangonzalezquintero@gmail.com` (`SUPER_ADMIN`) o `sofia@logitrack.com` (`GERENTE_LOGISTICA`).

#### 📋 Pasos de Ejecución:
1. Ir al módulo de **Reportes**.
2. En la sección de exportación, presionar **`[ 📧 Enviar Reporte Diario - Correo a Admins / Gerentes ]`**.
3. Confirmar en el cuadro modal de diálogo.

#### ✅ Criterios de Aceptación & Resultado Esperado:
- Se muestra la notificación Toast: *"Generando informe y enviando correos..."*.
- Los correos registrados con rol `SUPER_ADMIN`, `ADMIN` y `GERENTE_LOGISTICA` reciben el informe ejecutivo HTML formateado en su bandeja de entrada.
