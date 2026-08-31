# 📦 LogiTrack S.A. — La Gran Guía de la Lógica del Negocio
> *Una explicación clara, paso a paso y didáctica de cómo funciona todo el sistema de inventarios y logística (WMS / ERP), explicada desde cero de forma muy intuitiva pero con todo el rigor técnico.*

---

## 🌟 1. ¿Qué es LogiTrack S.A. en palabras simples?

Imagina que tienes la **empresa de tecnología y logística más grande del país**.
Tienes miles de cajas de videojuegos, laptops, sillas, teclados y monitores repartidos en 5 bodegas gigantes en Bogotá, Medellín, Cali, Barranquilla y Bucaramanga.

Si no tuvieras un sistema inteligente:
- ❌ No sabrías en qué estante exacto guardaste cada caja.
- ❌ Los productos con fecha de vencimiento se dañarían en el fondo del almacén.
- ❌ Dos clientes comprarían la misma laptop al mismo tiempo sin haber inventario suficiente.
- ❌ Si alguien borrara o cambiara un número, nadie sabría quién fue ni a qué hora.

**LogiTrack S.A.** es el "cerebro digital" que controla todo: sabe exactamente qué entra, dónde se guarda, quién lo movió, cuándo se vence, cómo se prepara el pedido y en qué camión viaja hasta la puerta del cliente.

---

## 🗺️ 2. El Mapa del Tesoro: Las Piezas del Sistema

Para entender el negocio, conozcamos cómo se conectan los módulos entre sí:

```
[🏭 Proveedor]
      │ (1. Vende y envía mercancía)
      ▼
[📝 Orden de Compra (OC)] ──► [📥 Recepción / Movimiento ENTRADA]
                                           │
                                           ▼
                             [🏬 Bodegas & Zonas de Almacén]
                                           │
                                           ▼
                             [📍 Ubicaciones Físicas & Racks]
                                           │
                                           ▼
                             [🏷️ Control por Lotes (FEFO) y Series]
                                           │
[👤 Cliente Final]                         │ (Inventario Disponible)
      │                                    │
      ▼ (2. Solicita productos)            │
[🛍️ Pedido de Cliente] ◄───────────────────┘
      │
      ▼ (3. Se genera orden de recolección)
[🛒 Tareas de Picking en Pasillos]
      │
      ▼ (4. Empaque y despacho)
[🚚 Guías de Despacho & Transportadoras] ──► [Entrega al Cliente ✓]
```

---

## 🔄 3. El Viaje de un Producto: De Principio a Fin (Flujo Transaccional)

Acompañemos a una **Laptop Corporativa** en todo su viaje dentro de LogiTrack:

### 🧩 Paso 1: Pedir mercancía al fabricante (Órdenes de Compra)
1. El **Jefe de Compras** nota que el stock de laptops está bajo.
2. Crea una **Orden de Compra (`OC-20260827-001`)** dirigida al proveedor (*TechSupplies S.A.S.*) pidiendo 5 laptops para la *Bodega Central*.
3. La orden nace en estado **`PENDIENTE`**.
4. El Gerente o Supervisor la revisa y hace clic en **`APROBAR`**.
5. El fabricante recibe la orden y envía su camión a nuestras instalaciones.

### 🧩 Paso 2: Llegada a la bodega y recepción (Movimiento de Entrada)
1. El camión llega al muelle de la bodega.
2. El operario abre la orden aprobada y hace clic en **`RECIBIR`**.
3. **Magia automática del sistema:**
   - Se crea automáticamente un **Movimiento de `ENTRADA`**.
   - El stock general del producto sube (+5 unidades).
   - El inventario específico de esa bodega sube (+5 unidades).
   - La Orden de Compra pasa a estado **`RECIBIDA`**.
   - La **Auditoría** guarda: *"Carlos González recibió 5 laptops el 27 de agosto"*.

### 🧩 Paso 3: Guardar en el estante exacto (Zonas, Ubicaciones y Series)
Para no perder la caja en una bodega gigante:
- Se asigna a la zona **`ZN-B1-ALM`** (Almacenamiento Racks Altos).
- Se coloca en la posición **`B1-PAS1-RACK-A1`** (Bodega 1, Pasillo 1, Estante A, Nivel 1).
- A cada laptop se le registra su **Número de Serie único** (ej: `SN-DELL-XPS-001`) para saber exactamente cuál equipo físico tiene cada cliente.

### 🧩 Paso 4: Trazabilidad y Fechas de Vencimiento (Lotes & Regla FEFO)
Para productos con caducidad (como baterías, tintas o resmas):
- Se les asigna un **Lote** (`LOT-2026-01`) con su fecha de fabricación y vencimiento.
- LogiTrack utiliza la regla **FEFO** (*First Expired, First Out* / *El que vence primero, sale primero*).
- Si un lote está por vencer en menos de 30 días, el sistema enciende una **alerta amarilla de atención**, para que los operarios le den prioridad de salida antes que a los lotes nuevos.

### 🧩 Paso 5: Un cliente hace un pedido (Pedidos de Clientes)
1. La empresa cliente *Soluciones Digitales ABC* solicita 2 laptops.
2. Se crea el pedido **`PED-20260828-001`** en estado **`PENDIENTE`**.
3. El sistema valida automáticamente que haya stock suficiente en la bodega origen.

### 🧩 Paso 6: Ir a buscar las cosas al estante (Picking Operativo)
1. El sistema genera automáticamente una **Tarea de Picking** (`PCK-1-1`).
2. La tarea le indica al operario: *"Ve al Pasillo 1, Rack A, Nivel 1 y toma 2 laptops"*.
3. El operario confirma las unidades recolectadas en el sistema.
4. El pedido avanza de estado: **`PENDIENTE`** ➔ **`EN_PREPARACION`** ➔ **`EMPACADO`**.

### 🧩 Paso 7: Despachar en el camión de reparto (Guías y Despachos)
1. Se genera una **Guía de Despacho** (`GUIA-COL-20260828-01`).
2. Se asigna la transportadora (*LogiExpress Colombia*), conductor y placa del vehículo.
3. Al hacer clic en **`DESPACHAR`**:
   - Se crea un **Movimiento de `SALIDA`** del inventario.
   - El stock de la bodega se descuenta automáticamente (-2 unidades).
   - El pedido pasa a **`DESPACHADO`** y la guía a **`EN_TRANSITO`**.
4. Cuando el cliente recibe el paquete, se marca como **`ENTREGADO`**.

---

## 🛡️ 4. ¿Qué pasa si algo sale mal? (Mermas, Ajustes y Conteos)

En el día a día pueden ocurrir accidentes o diferencias físicas:

### ⚠️ A. Si se rompe una caja o se daña una pieza (Ajustes / Mermas)
- Un operario tropezó y se averió un producto.
- Se registra un **Ajuste de Inventario** de tipo **`MERMA`** o **`DANO`** con justificación obligatoria (*"Daño en transporte interno"*).
- El sistema descuenta la unidad averiada del stock y deja registro histórico inalterable.

### 🔍 B. Si los números no cuadran con la realidad (Conteos Cíclicos)
- Periódicamente, el Supervisor programa una **Auditoría Física**.
- El operario cuenta a mano lo que hay en las estanterías y lo digita en la plataforma.
- Si el sistema tenía 55 y en el estante hay 54, el sistema calcula la diferencia (-1) y permite **conciliar** el inventario con un clic.

### 🚨 C. Si se están agotando las existencias (Alertas de Bajo Stock)
- Cada producto tiene un **Stock Mínimo** de seguridad.
- Si las existencias bajan de ese umbral, el sistema activa una **Alerta de Stock** en el panel principal y notifica a compras para reabastecer a tiempo.

---

## 🕵️‍♂️ 5. La Cámara de Seguridad: El Sistema de Auditoría

En LogiTrack **NADA ocurre en secreto**.
Cada vez que un usuario:
- Crea un producto o bodega (`INSERT`)
- Modifica un precio o cambia un stock (`UPDATE`)
- Desactiva un registro (`DELETE`)

El sistema guarda automáticamente:
- 👤 **¿Quién fue?** (Email del usuario)
- ⏰ **¿Cuándo ocurrió?** (Fecha y hora exacta con microsegundos)
- 📄 **¿Qué cambió?** (Valores anteriores vs. valores nuevos en formato JSON)
- 📝 **¿Qué acción fue?** (Descripción en español claro)

---

## 👥 6. Los Roles: ¿Quién tiene la llave de cada puerta?

LogiTrack utiliza seguridad **RBAC** (*Role-Based Access Control*):

| Rol | ¿Quién es? | ¿Qué permisos tiene? |
| :--- | :--- | :--- |
| 👑 **`ADMIN`** | El Administrador General | Control total: crea usuarios, bodegas, aprueba todo, edita y elimina. |
| 🛡️ **`SUPERVISOR`** | El Jefe de Bodega | Supervisa operaciones, ejecuta conteos cíclicos, autoriza ajustes y aprueba órdenes. |
| 📦 **`EMPLEADO`** | El Operario de Bodega | Registra entradas, salidas, traslados, hace picking y cuenta inventario físico. |
| 🛒 **`JEFE_COMPRAS`** | El Encargado de Adquisiciones | Crea productos, gestiona proveedores y emite órdenes de compra. |
| 🚚 **`GERENTE_LOGISTICA`** | El Director de Distribución | Monitorea despachos, rutas de transportadoras, clientes y métricas globales. |

---

## 📊 7. El Panel del Capitán: Dashboard y Reportes

Al entrar a LogiTrack, la primera pantalla muestra el estado de la operación en tiempo real:
- 🏬 **Total de Bodegas Activas** y su porcentaje de ocupación visual con medidores dinámicos.
- 📦 **Total de Stock Físico Disponible** sumando todas las bodegas.
- ⚡ **Movimientos Recientes** (Entradas verdes, Salidas rojas, Transferencias amarillas).
- 🚨 **Semáforo de Alertas** con acceso directo para resolver productos con stock crítico.
- 📈 **Reportes Inteligentes:**
  - **Matriz ABC:** Identifica el 20% de productos que representan el 80% del valor total (Principio de Pareto).
  - **Exportación:** Descarga de reportes en PDF y Excel para la gerencia.

---

## 🎯 8. Resumen en 3 Reglas de Oro

1. **Unicidad:** Cada bodega, orden, lote y número de serie tiene un código único irrepetible.
2. **Trazabilidad:** Todo movimiento tiene origen, destino, responsable y justificación.
3. **Integridad:** El inventario nunca miente; cada cambio genera un registro de auditoría instantáneo.

---
*LogiTrack S.A. — Arquitectura Empresarial WMS / ERP.*
