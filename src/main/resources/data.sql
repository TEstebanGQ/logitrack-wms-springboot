-- ============================================================
-- LogiTrack S.A. - data.sql
-- Datos iniciales y transaccionales completos (PostgreSQL)
-- 28 Tablas Integrales WMS / ERP (Ayer y Hoy)
-- ============================================================

-- ---- 1. CATEGORÍAS ----
INSERT INTO categorias (id, nombre, descripcion, activo, created_at) VALUES
(1, 'Electrónica',          'Equipos tecnológicos, laptops, monitores, servidores', true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(2, 'Periféricos',           'Teclados, mouses, auriculares y accesorios',           true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(3, 'Mobiliario',            'Muebles de oficina, sillas ergonómicas, escritorios',  true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(4, 'Papelería & Empaque',   'Artículos de oficina, cajas, resmas y etiquetas',      true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(5, 'Insumos Industriales',  'Herramientas, cintas de embalaje y seguridad',         true, CURRENT_TIMESTAMP - INTERVAL '10 days')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('categorias', 'id'), COALESCE((SELECT MAX(id) FROM categorias), 1));

-- ---- 2. UNIDADES DE MEDIDA ----
INSERT INTO unidades_medida (id, codigo, nombre, abreviatura, factor_conversion, activo, created_at) VALUES
(1, 'UND', 'Unidad Individual', 'u.',  1.0,  true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(2, 'CJ',  'Caja Estándar',     'cj.', 12.0, true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(3, 'PLT', 'Palet Americano',   'plt.',48.0, true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(4, 'KG',  'Kilogramo',         'kg',  1.0,  true, CURRENT_TIMESTAMP - INTERVAL '10 days')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('unidades_medida', 'id'), COALESCE((SELECT MAX(id) FROM unidades_medida), 1));

-- ---- 3. USUARIOS (contraseñas: 'admin123', 'empleado123' - BCrypt) ----
INSERT INTO usuarios (id, nombre, apellido, email, password, rol, activo, created_at) VALUES
(1, 'Admin',    'Sistema',    'admin@logitrack.com',    '$2a$10$r//FnmN.GUtMhQtM23FUhOUBQOwQZjElEu/.LXN701rSgFGKQUHpm', 'ADMIN',             true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(2, 'Carlos',   'González',   'carlos@logitrack.com',   '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'EMPLEADO',          true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(3, 'María',    'López',      'maria@logitrack.com',    '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'EMPLEADO',          true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(4, 'Andrés',   'Martínez',   'andres@logitrack.com',   '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'EMPLEADO',          true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(5, 'Laura',    'Pérez',      'laura@logitrack.com',    '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'SUPERVISOR',        true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(6, 'Sofía',    'Ramírez',    'sofia@logitrack.com',    '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'GERENTE_LOGISTICA', true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(7, 'Pedro',    'Sánchez',    'pedro@logitrack.com',    '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'JEFE_COMPRAS',      true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(8, 'Tomás Esteban', 'González Quintero', 'tomasestebangonzalezquintero@gmail.com', '$2a$10$r//FnmN.GUtMhQtM23FUhOUBQOwQZjElEu/.LXN701rSgFGKQUHpm', 'SUPER_ADMIN', true, CURRENT_TIMESTAMP - INTERVAL '10 days')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('usuarios', 'id'), COALESCE((SELECT MAX(id) FROM usuarios), 1));

-- ---- 4. BODEGAS ----
INSERT INTO bodegas (id, nombre, ubicacion, capacidad, encargado, activo, created_at) VALUES
(1, 'Bodega Central',    'Bogotá, Cundinamarca - Zona Industrial', 5000, 'Carlos González',  true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(2, 'Bodega Norte',      'Medellín, Antioquia - Guayabal',         3000, 'María López',       true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(3, 'Bodega Sur',        'Cali, Valle del Cauca - Acopi Yumbo',    4000, 'Andrés Martínez',   true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(4, 'Bodega Caribe',     'Barranquilla, Atlántico - Vía 40',       2000, 'Pedro Ramírez',     true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(5, 'Bodega Oriente',    'Bucaramanga, Santander - Chimitá',       3500, 'Ana Torres',        true, CURRENT_TIMESTAMP - INTERVAL '10 days')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('bodegas', 'id'), COALESCE((SELECT MAX(id) FROM bodegas), 1));

-- ---- 5. ZONAS DE BODEGA ----
INSERT INTO zonas_bodega (id, codigo, nombre, tipo_zona, bodega_id, temperatura_controlada, descripcion, activo, created_at) VALUES
(1, 'ZN-B1-REC',  'Zona de Recepción y Muelles',  'RECEPCION',      1, false, 'Área de descarga y verificación inicial', true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(2, 'ZN-B1-ALM',  'Almacenamiento Racks Altos',   'ALMACENAMIENTO', 1, false, 'Pasillos 1 al 4 para racks pesados',    true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(3, 'ZN-B1-PCK',  'Zona de Picking Rápido',       'PICKING',        1, false, 'Estantería nivel suelo para pedidos',    true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(4, 'ZN-B1-DSP',  'Zona de Embalaje y Despacho',  'DESPACHO',       1, false, 'Consolidación de envíos y albaranes',    true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(5, 'ZN-B2-ALM',  'Almacenamiento General Norte', 'ALMACENAMIENTO', 2, false, 'Bodega principal Medellín',             true, CURRENT_TIMESTAMP - INTERVAL '10 days')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('zonas_bodega', 'id'), COALESCE((SELECT MAX(id) FROM zonas_bodega), 1));

-- ---- 6. TIPOS DE UBICACIÓN ----
INSERT INTO tipos_ubicacion (id, codigo, nombre, peso_maximo_kg, volumen_maximo_m3, descripcion, activo, created_at) VALUES
(1, 'RCK-PESADO', 'Rack Industrial Pesado', 2500.0, 5.0, 'Para pallets con servidores y maquinaria', true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(2, 'EST-MEDIA',  'Estantería Carga Media', 800.0,  2.0, 'Para laptops y periféricos',         true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(3, 'PCK-LIV',    'Gaveta de Picking',      150.0,  0.5, 'Para cables, mouses y accesorios',   true, CURRENT_TIMESTAMP - INTERVAL '10 days')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('tipos_ubicacion', 'id'), COALESCE((SELECT MAX(id) FROM tipos_ubicacion), 1));

-- ---- 7. UBICACIONES FÍSICAS DE BODEGA ----
INSERT INTO ubicaciones_bodega (id, codigo_ubicacion, pasillo, estante, nivel, capacidad_max, descripcion, bodega_id, activo, created_at) VALUES
(1, 'B1-PAS1-RACK-A1', 'Pasillo 1', 'Rack A', 'Nivel 1 (Bajo)', 200, 'Zona frontal alta rotación laptops', 1, true, CURRENT_TIMESTAMP - INTERVAL '5 days'),
(2, 'B1-PAS1-RACK-A2', 'Pasillo 1', 'Rack A', 'Nivel 2 (Medio)', 200, 'Almacenamiento monitores',          1, true, CURRENT_TIMESTAMP - INTERVAL '5 days'),
(3, 'B1-PAS2-RACK-B1', 'Pasillo 2', 'Rack B', 'Nivel 1 (Bajo)', 300, 'Mobiliario y sillas de oficina',    1, true, CURRENT_TIMESTAMP - INTERVAL '5 days'),
(4, 'B1-PAS3-RACK-C1', 'Pasillo 3', 'Rack C', 'Nivel 1 (Bajo)', 500, 'Periféricos y papelería compacta',   1, true, CURRENT_TIMESTAMP - INTERVAL '5 days'),
(5, 'B2-PAS1-RACK-A1', 'Pasillo 1', 'Rack A', 'Nivel 1',        150, 'Recepción Bodega Medellín',          2, true, CURRENT_TIMESTAMP - INTERVAL '5 days'),
(6, 'B3-PAS1-RACK-A1', 'Pasillo 1', 'Rack A', 'Nivel 1',        200, 'Recepción Bodega Cali',              3, true, CURRENT_TIMESTAMP - INTERVAL '5 days')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('ubicaciones_bodega', 'id'), COALESCE((SELECT MAX(id) FROM ubicaciones_bodega), 1));

-- ---- 8. PRODUCTOS ----
INSERT INTO productos (id, nombre, categoria_id, stock, stock_minimo, precio, descripcion, activo, created_at) VALUES
(1,  'Laptop Dell XPS 15',     1, 55,  10, 4500000.00, 'Laptop empresarial Core i7 16GB 512GB SSD', true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(2,  'Monitor LG 27"',         1, 32,  10, 900000.00,  'Monitor Full HD IPS 75Hz con HDMI',          true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(3,  'Teclado Mecánico',       2, 85,  15, 250000.00,  'Teclado mecánico retroiluminado Switch Red', true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(4,  'Mouse Inalámbrico',      2, 130, 20, 85000.00,   'Mouse ergonómico receptor USB y Bluetooth',  true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(5,  'Silla Ergonómica',       3, 20,  5,  1200000.00, 'Silla ejecutiva con soporte lumbar 3D',      true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(6,  'Escritorio Ejecutivo',   3, 12,  5,  850000.00,  'Escritorio en L 180x140cm estructura acero', true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(7,  'Papel A4 500 hojas',     4, 250, 50, 18000.00,   'Resma papel bond premium 75g multicopia',    true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(8,  'Bolígrafos x12',         4, 340, 40, 12000.00,   'Caja de 12 bolígrafos micropunta azul',      true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(9,  'Impresora HP LaserJet',  1, 8,   10, 1800000.00, 'Impresora láser monocromática dúplex',       true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(10, 'Auriculares Bluetooth',  2, 48,  10, 320000.00,  'Auriculares inalámbricos con ANC híbrido',   true, CURRENT_TIMESTAMP - INTERVAL '10 days')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('productos', 'id'), COALESCE((SELECT MAX(id) FROM productos), 1));

-- ---- 9. NÚMEROS DE SERIE (Trazabilidad Unitaria) ----
INSERT INTO producto_series (id, numero_serie, producto_id, bodega_id, ubicacion_id, estado, fecha_ingreso, fecha_despacho, observaciones) VALUES
(1, 'SN-DELL-XPS-001', 1, 1, 1, 'EN_STOCK',   CURRENT_TIMESTAMP - INTERVAL '1 day 5 hours', null, 'Ingresado por lote'),
(2, 'SN-DELL-XPS-002', 1, 1, 1, 'EN_STOCK',   CURRENT_TIMESTAMP - INTERVAL '1 day 5 hours', null, 'Ingresado por lote'),
(3, 'SN-DELL-XPS-003', 1, 1, 1, 'DESPACHADO', CURRENT_TIMESTAMP - INTERVAL '1 day 5 hours', CURRENT_TIMESTAMP - INTERVAL '2 hours', 'Despachado a Soluciones ABC'),
(4, 'SN-LG-MON-001',   2, 1, 2, 'EN_STOCK',   CURRENT_TIMESTAMP - INTERVAL '1 day 3 hours', null, 'Verificado en control de calidad')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('producto_series', 'id'), COALESCE((SELECT MAX(id) FROM producto_series), 1));

-- ---- 10. INVENTARIO POR BODEGA ----
INSERT INTO inventario_bodega (bodega_id, producto_id, stock_actual, ultima_actualizacion) VALUES
(1, 1, 25, CURRENT_TIMESTAMP), (1, 2, 12, CURRENT_TIMESTAMP), (1, 3, 35, CURRENT_TIMESTAMP), (1, 4, 55, CURRENT_TIMESTAMP), (1, 5, 12, CURRENT_TIMESTAMP),
(1, 6,  7, CURRENT_TIMESTAMP), (1, 7, 90, CURRENT_TIMESTAMP), (1, 8,110, CURRENT_TIMESTAMP), (1, 9,  4, CURRENT_TIMESTAMP), (1,10, 22, CURRENT_TIMESTAMP),
(2, 1, 15, CURRENT_TIMESTAMP), (2, 2,  8, CURRENT_TIMESTAMP), (2, 3, 20, CURRENT_TIMESTAMP), (2, 4, 35, CURRENT_TIMESTAMP), (2, 5,  8, CURRENT_TIMESTAMP),
(2, 7, 60, CURRENT_TIMESTAMP), (2, 8, 80, CURRENT_TIMESTAMP), (2,10, 15, CURRENT_TIMESTAMP),
(3, 1, 10, CURRENT_TIMESTAMP), (3, 2,  7, CURRENT_TIMESTAMP), (3, 4, 25, CURRENT_TIMESTAMP), (3, 6,  5, CURRENT_TIMESTAMP), (3, 7, 40, CURRENT_TIMESTAMP),
(3, 8, 80, CURRENT_TIMESTAMP), (3, 9,  4, CURRENT_TIMESTAMP), (3,10, 11, CURRENT_TIMESTAMP),
(4, 3, 15, CURRENT_TIMESTAMP), (4, 4, 15, CURRENT_TIMESTAMP), (4, 7, 30, CURRENT_TIMESTAMP), (4, 8, 40, CURRENT_TIMESTAMP),
(5, 1,  5, CURRENT_TIMESTAMP), (5, 2,  5, CURRENT_TIMESTAMP), (5, 3, 15, CURRENT_TIMESTAMP), (5, 7, 30, CURRENT_TIMESTAMP), (5, 8, 30, CURRENT_TIMESTAMP)
ON CONFLICT (bodega_id, producto_id) DO UPDATE SET stock_actual = EXCLUDED.stock_actual, ultima_actualizacion = CURRENT_TIMESTAMP;

-- ---- 11. PROVEEDORES ----
INSERT INTO proveedores (id, nombre, ruc, telefono, email, direccion, activo, created_at) VALUES
(1, 'TechSupplies Colombia S.A.S.',  '900123456-1', '+57 601 555 0001', 'ventas@techsupplies.co',    'Cra 15 # 93-47 Oficina 502, Bogotá',  true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(2, 'OfficeWorld & Muebles Ltda.',   '800987654-2', '+57 604 555 0002', 'pedidos@officeworld.co',   'Cl 50 # 43-65, Medellín',             true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(3, 'Distribuidora Papelera Andina', '700456789-3', '+57 602 555 0003', 'contacto@papelandina.co',   'Av 3N # 23-45, Cali',                 true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(4, 'Logística & Periféricos Global','901222333-4', '+57 605 555 0004', 'ventas@globalperif.com',    'Vía 40 # 73-290, Barranquilla',       true, CURRENT_TIMESTAMP - INTERVAL '10 days')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('proveedores', 'id'), COALESCE((SELECT MAX(id) FROM proveedores), 1));

-- ---- 12. CLIENTES ----
INSERT INTO clientes (id, nombre, ruc, telefono, email, direccion, activo, created_at) VALUES
(1, 'Soluciones Digitales ABC S.A.S.', '900111222-1', '+57 601 444 0001', 'compras@empresaabc.co',   'Cl 72 # 10-35, Bogotá',               true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(2, 'Constructora & Diseños XYZ',      '800333444-2', '+57 604 444 0002', 'logistica@xyzcorp.co',    'Cl 30 # 65-20, Medellín',             true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(3, 'Comercializadora del Pacífico',   '700555666-3', '+57 602 444 0003', 'admin@compacifico.co',    'Cr 1 # 12-30, Cali',                  true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(4, 'Inversiones Tecnológicas Caribe', '901777888-4', '+57 605 444 0004', 'adquisiciones@tecari.com', 'Calle 84 # 51B-32, Barranquilla',     true, CURRENT_TIMESTAMP - INTERVAL '10 days')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('clientes', 'id'), COALESCE((SELECT MAX(id) FROM clientes), 1));

-- ---- 13. TRANSPORTADORAS ----
INSERT INTO transportadoras (id, nombre, ruc_nit, telefono, email, contacto, tipo_servicio, activo, created_at) VALUES
(1, 'LogiExpress Colombia S.A.',   '900999888-1', '+57 601 888 1111', 'despachos@logiexpress.co', 'Fernando Vega', 'TERRESTRE',    true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(2, 'AeroCarga Nacional Express',  '900777666-2', '+57 601 888 2222', 'operaciones@aerocarga.co', 'Claudia Rivas', 'AEREO',        true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(3, 'Flota LogiTrack Distribución','900111000-3', '+57 601 888 3333', 'flota@logitrack.com',      'Mario Ruiz',    'FLOTA_PROPIA', true, CURRENT_TIMESTAMP - INTERVAL '10 days')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('transportadoras', 'id'), COALESCE((SELECT MAX(id) FROM transportadoras), 1));

-- ---- 14. PEDIDOS DE CLIENTES (Ayer y Hoy) ----
INSERT INTO pedidos_cliente (id, codigo_pedido, cliente_id, bodega_origen_id, estado, total_pedido, fecha_pedido, fecha_compromiso, direccion_entrega, observaciones, usuario_creador_id) VALUES
(1, 'PED-20260827-001', 1, 1, 'DESPACHADO', 9000000.00, CURRENT_TIMESTAMP - INTERVAL '1 day 4 hours', CURRENT_DATE + INTERVAL '2 days', 'Cl 72 # 10-35, Bogotá',   'Entrega prioritaria', 1),
(2, 'PED-20260828-001', 2, 2, 'EN_PREPARACION', 425000.00, CURRENT_TIMESTAMP - INTERVAL '3 hours',       CURRENT_DATE + INTERVAL '3 days', 'Cl 30 # 65-20, Medellín', 'Despacho de mouses', 1),
(3, 'PED-20260828-002', 3, 1, 'PENDIENTE', 1800000.00, CURRENT_TIMESTAMP - INTERVAL '1 hour',        CURRENT_DATE + INTERVAL '4 days', 'Cr 1 # 12-30, Cali',      'Solicitud de resmas de papel', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO pedido_cliente_detalles (id, pedido_id, producto_id, cantidad_solicitada, cantidad_despachada, precio_unitario, subtotal) VALUES
(1, 1, 1, 2, 2, 4500000.00, 9000000.00),
(2, 2, 4, 5, 0,   85000.00,  425000.00),
(3, 3, 7, 100, 0,  18000.00, 1800000.00)
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('pedidos_cliente', 'id'), COALESCE((SELECT MAX(id) FROM pedidos_cliente), 1));
SELECT setval(pg_get_serial_sequence('pedido_cliente_detalles', 'id'), COALESCE((SELECT MAX(id) FROM pedido_cliente_detalles), 1));

-- ---- 15. GUÍAS DE DESPACHO Y TRACKING ----
INSERT INTO guias_despacho (id, numero_guia, pedido_id, transportadora_id, fecha_despacho, fecha_entrega_estimada, fecha_entrega_real, estado_envio, conductor_nombre, placa_vehiculo, costo_flete, observaciones) VALUES
(1, 'GUIA-COL-20260827-01', 1, 1, CURRENT_TIMESTAMP - INTERVAL '1 day 2 hours', CURRENT_DATE + INTERVAL '1 day', null, 'EN_TRANSITO', 'Javier Peña', 'TRK-456', 45000.00, 'En ruta hacia Bogotá Norte')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('guias_despacho', 'id'), COALESCE((SELECT MAX(id) FROM guias_despacho), 1));

-- ---- 16. TAREAS DE PICKING OPERATIVO ----
INSERT INTO tareas_picking (id, codigo_tarea, pedido_id, producto_id, ubicacion_origen_id, usuario_asignado_id, cantidad_requerida, cantidad_recogida, estado, fecha_asignacion, fecha_completada, notas) VALUES
(1, 'PCK-1-1', 1, 1, 1, 2, 2, 2, 'COMPLETADA', CURRENT_TIMESTAMP - INTERVAL '1 day 3 hours', CURRENT_TIMESTAMP - INTERVAL '1 day 2 hours', 'Picking exitoso'),
(2, 'PCK-2-1', 2, 4, 4, 3, 5, 3, 'EN_PROCESO',  CURRENT_TIMESTAMP - INTERVAL '2 hours',       null, 'Recolección parcial de 3 mouses'),
(3, 'PCK-3-1', 3, 7, 4, 4, 100, 0, 'PENDIENTE', CURRENT_TIMESTAMP - INTERVAL '45 minutes',   null, 'Pendiente de inicio en pasillo 3')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('tareas_picking', 'id'), COALESCE((SELECT MAX(id) FROM tareas_picking), 1));

-- ---- 17. CONTEOS CÍCLICOS Y AUDITORÍA FÍSICA ----
INSERT INTO conteos_ciclicos (id, codigo_conteo, bodega_id, zona_id, fecha_programada, fecha_ejecucion, estado, supervisor_id, observaciones) VALUES
(1, 'AUD-20260827-01', 1, 2, CURRENT_DATE - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day 2 hours', 'CERRADO',    5, 'Auditoría de alta rotación pasillo 1 completada'),
(2, 'AUD-20260828-01', 1, 3, CURRENT_DATE,                   CURRENT_TIMESTAMP - INTERVAL '1 hour',        'EN_PROCESO', 5, 'Conteo de estantería de periféricos')
ON CONFLICT (id) DO NOTHING;

INSERT INTO conteo_ciclico_detalles (id, conteo_id, producto_id, ubicacion_id, stock_sistema, stock_fisico, diferencia, estado_linea, notas) VALUES
(1, 1, 1, 1, 25, 25,  0, 'CONCILIADO', 'Stock conforme'),
(2, 1, 2, 2, 12, 12,  0, 'CONCILIADO', 'Stock conforme'),
(3, 2, 4, 4, 55, 54, -1, 'RECONTAR',   'Posible unidad extraviada en picking')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('conteos_ciclicos', 'id'), COALESCE((SELECT MAX(id) FROM conteos_ciclicos), 1));
SELECT setval(pg_get_serial_sequence('conteo_ciclico_detalles', 'id'), COALESCE((SELECT MAX(id) FROM conteo_ciclico_detalles), 1));

-- ---- 18. LOTES Y TRAZABILIDAD FEFO ----
INSERT INTO lotes (id, codigo_lote, producto_id, bodega_id, stock_inicial, stock_actual, fecha_fabricacion, fecha_vencimiento, estado, created_at) VALUES
(1, 'LOT-DELL-2026-01', 1, 1, 30, 25, CURRENT_DATE - INTERVAL '15 days', CURRENT_DATE + INTERVAL '180 days', 'DISPONIBLE', CURRENT_TIMESTAMP - INTERVAL '1 day 4 hours'),
(2, 'LOT-LG-2026-01',   2, 1, 15, 12, CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE + INTERVAL '365 days', 'DISPONIBLE', CURRENT_TIMESTAMP - INTERVAL '1 day 3 hours'),
(3, 'LOT-TEC-2026-02',  3, 1, 40, 35, CURRENT_DATE - INTERVAL '10 days', CURRENT_DATE + INTERVAL '240 days', 'DISPONIBLE', CURRENT_TIMESTAMP - INTERVAL '5 hours'),
(4, 'LOT-PAPEL-2026-A', 7, 1, 100, 90, CURRENT_DATE - INTERVAL '30 days', CURRENT_DATE + INTERVAL '25 days',  'DISPONIBLE', CURRENT_TIMESTAMP - INTERVAL '2 hours'),
(5, 'LOT-IMP-2026-01',  9, 1, 5,   4,  CURRENT_DATE - INTERVAL '40 days', CURRENT_DATE + INTERVAL '20 days',  'DISPONIBLE', CURRENT_TIMESTAMP - INTERVAL '1 hour')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('lotes', 'id'), COALESCE((SELECT MAX(id) FROM lotes), 1));

-- ---- 19. ÓRDENES DE COMPRA (Ayer y Hoy) ----
INSERT INTO ordenes_compra (id, codigo_orden, proveedor_id, bodega_destino_id, estado, total_estimado, fecha_solicitud, fecha_entrega_esperada, observaciones, usuario_solicitante_id) VALUES
(1, 'OC-20260827-001', 1, 1, 'RECIBIDA',  22500000.00, CURRENT_TIMESTAMP - INTERVAL '1 day 6 hours', CURRENT_DATE + INTERVAL '5 days', 'Pedido urgente de Laptops Dell XPS para reposición', 7),
(2, 'OC-20260827-002', 2, 1, 'APROBADA',   6000000.00, CURRENT_TIMESTAMP - INTERVAL '1 day 2 hours', CURRENT_DATE + INTERVAL '7 days', 'Reabastecimiento de mobiliario corporativo',     7),
(3, 'OC-20260828-001', 3, 1, 'PENDIENTE',  1800000.00, CURRENT_TIMESTAMP - INTERVAL '3 hours',       CURRENT_DATE + INTERVAL '3 days', 'Pedido mensual de resmas y papelería',            7),
(4, 'OC-20260828-002', 1, 2, 'PENDIENTE',  4500000.00, CURRENT_TIMESTAMP - INTERVAL '1 hour',        CURRENT_DATE + INTERVAL '4 days', 'Monitores adicionales para sede Medellín',         7)
ON CONFLICT (id) DO NOTHING;

INSERT INTO ordenes_compra_detalles (id, orden_compra_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
(1, 1, 1, 5, 4500000.00, 22500000.00),
(2, 2, 5, 5, 1200000.00,  6000000.00),
(3, 3, 7, 100, 18000.00,  1800000.00),
(4, 4, 2, 5, 900000.00,   4500000.00)
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('ordenes_compra', 'id'), COALESCE((SELECT MAX(id) FROM ordenes_compra), 1));
SELECT setval(pg_get_serial_sequence('ordenes_compra_detalles', 'id'), COALESCE((SELECT MAX(id) FROM ordenes_compra_detalles), 1));

-- ---- 20. MOVIMIENTOS DE INVENTARIO (Ayer y Hoy) ----
INSERT INTO movimientos (id, tipo_movimiento, fecha, observaciones, usuario_id, bodega_origen_id, bodega_destino_id, proveedor_id, cliente_id) VALUES
(1, 'ENTRADA',        CURRENT_TIMESTAMP - INTERVAL '1 day 5 hours', 'Ingreso de mercancía por OC-20260827-001', 1, null, 1, 1, null),
(2, 'SALIDA',         CURRENT_TIMESTAMP - INTERVAL '1 day 3 hours', 'Despacho comercial pedido corporativo',     2, 1, null, null, 1),
(3, 'TRANSFERENCIA',  CURRENT_TIMESTAMP - INTERVAL '1 day 1 hour',  'Reubicación de stock de Bogotá a Medellín', 5, 1, 2, null, null),
(4, 'ENTRADA',        CURRENT_TIMESTAMP - INTERVAL '4 hours',       'Recepción de periféricos y auriculares',    1, null, 1, 4, null),
(5, 'SALIDA',         CURRENT_TIMESTAMP - INTERVAL '2 hours',       'Despacho cliente Constructora XYZ',         2, 2, null, null, 2),
(6, 'TRANSFERENCIA',  CURRENT_TIMESTAMP - INTERVAL '45 minutes',    'Envío de seguridad de stock a Cali',        5, 1, 3, null, null)
ON CONFLICT (id) DO NOTHING;

INSERT INTO movimiento_detalle (id, movimiento_id, producto_id, cantidad, precio_unitario) VALUES
(1, 1, 1, 5, 4500000.00),
(2, 2, 1, 2, 4500000.00),
(3, 3, 3, 10, 250000.00),
(4, 4, 10, 8, 320000.00),
(5, 5, 4, 5, 85000.00),
(6, 6, 2, 3, 900000.00)
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('movimientos', 'id'), COALESCE((SELECT MAX(id) FROM movimientos), 1));
SELECT setval(pg_get_serial_sequence('movimiento_detalle', 'id'), COALESCE((SELECT MAX(id) FROM movimiento_detalle), 1));

-- ---- 21. AJUSTES DE INVENTARIO Y MERMAS (Ayer y Hoy) ----
INSERT INTO ajustes_inventario (id, bodega_id, producto_id, tipo_ajuste, cantidad_anterior, cantidad_nueva, diferencia, justificacion, usuario_id, fecha) VALUES
(1, 1, 4, 'DANO',           57, 55, -2, 'Mouse con cable deteriorado en transporte interno', 1, CURRENT_TIMESTAMP - INTERVAL '1 day 4 hours'),
(2, 1, 7, 'CONTEO_FISICO',  88, 90, +2, 'Sobrante identificado en auditoría de estantería C', 5, CURRENT_TIMESTAMP - INTERVAL '1 day 2 hours'),
(3, 1, 9, 'VENCIMIENTO',     5,  4, -1, 'Tóner de prueba de fábrica vencido dado de baja',   5, CURRENT_TIMESTAMP - INTERVAL '3 hours'),
(4, 2, 3, 'MERMA',          21, 20, -1, 'Avería de switch en teclado durante picking',       1, CURRENT_TIMESTAMP - INTERVAL '1 hour')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('ajustes_inventario', 'id'), COALESCE((SELECT MAX(id) FROM ajustes_inventario), 1));

-- ---- 22. ALERTAS DE STOCK (Ayer y Hoy) ----
INSERT INTO alertas_stock (id, producto_id, bodega_id, stock_actual, stock_minimo, estado, fecha_generada, fecha_resuelta, resuelta_por_id) VALUES
(1, 9, 1, 4, 10, 'PENDIENTE', CURRENT_TIMESTAMP - INTERVAL '1 day 3 hours', null, null),
(2, 6, 3, 5, 10, 'PENDIENTE', CURRENT_TIMESTAMP - INTERVAL '5 hours', null, null),
(3, 1, 5, 5, 10, 'RESUELTA',  CURRENT_TIMESTAMP - INTERVAL '1 day 8 hours', CURRENT_TIMESTAMP - INTERVAL '1 day 2 hours', 1)
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('alertas_stock', 'id'), COALESCE((SELECT MAX(id) FROM alertas_stock), 1));

-- ---- 23. NOTIFICACIONES (Ayer y Hoy) ----
INSERT INTO notificaciones (id, usuario_id, titulo, mensaje, leida, tipo, fecha_creacion) VALUES
(1, 1, '⚠️ Alerta de Bajo Stock: Impresora HP', 'El stock de Impresora HP LaserJet en Bodega Central bajó a 4 unidades (Mín: 10).', false, 'ALERTA', CURRENT_TIMESTAMP - INTERVAL '1 day 3 hours'),
(2, 6, '📦 Orden de Compra Aprobada', 'Se aprobó la orden OC-20260827-002 para OfficeWorld por $6,000,000.', false, 'INFO', CURRENT_TIMESTAMP - INTERVAL '1 day 2 hours'),
(3, 1, '✓ Recepción Exitosa de Mercancía', 'La orden OC-20260827-001 fue recibida e ingresada al inventario de Bodega Central.', true, 'EXITO', CURRENT_TIMESTAMP - INTERVAL '1 day 5 hours'),
(4, 7, '📝 Nueva Solicitud de Compra', 'Se ha emitido la orden OC-20260828-001 pendiente de autorización.', false, 'INFO', CURRENT_TIMESTAMP - INTERVAL '3 hours'),
(5, 5, '⚠️ Alerta de Bajo Stock: Escritorio Ejecutivo', 'Bodega Sur tiene 5 escritorios restantes (Mín: 10).', false, 'ALERTA', CURRENT_TIMESTAMP - INTERVAL '5 hours')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('notificaciones', 'id'), COALESCE((SELECT MAX(id) FROM notificaciones), 1));

-- ---- 24. AUDITORÍA AUTOMÁTICA (Ayer y Hoy) ----
INSERT INTO auditoria (id, entidad, entidad_id, tipo_operacion, fecha_hora, usuario_id, valores_anteriores, valores_nuevos, ip_address, descripcion) VALUES
(1, 'Movimiento',       1, 'INSERT', CURRENT_TIMESTAMP - INTERVAL '1 day 5 hours', 1, null, '{"id":1,"tipo":"ENTRADA","bodegaDestino":"Bodega Central"}', '127.0.0.1', 'Registró movimiento ENTRADA en bodega ''Bodega Central'' - Productos: Laptop Dell XPS 15 (x5 u.)'),
(2, 'Producto',         1, 'UPDATE', CURRENT_TIMESTAMP - INTERVAL '1 day 5 hours', 1, '{"stock":50}', '{"stock":55}', '127.0.0.1', 'Actualización de stock por recepción de mercancía'),
(3, 'OrdenCompra',      1, 'UPDATE', CURRENT_TIMESTAMP - INTERVAL '1 day 5 hours', 1, '{"estado":"PENDIENTE"}', '{"estado":"RECIBIDA"}', '127.0.0.1', 'Recepción de orden de compra OC-20260827-001'),
(4, 'AjusteInventario', 1, 'INSERT', CURRENT_TIMESTAMP - INTERVAL '1 day 4 hours', 1, null, '{"tipo":"DANO","diferencia":-2}', '127.0.0.1', 'Ajuste por DANO en Bodega Central para Mouse Inalámbrico (Antes: 57, Nuevo: 55)'),
(5, 'Movimiento',       2, 'INSERT', CURRENT_TIMESTAMP - INTERVAL '1 day 3 hours', 2, null, '{"id":2,"tipo":"SALIDA","bodegaOrigen":"Bodega Central"}', '127.0.0.1', 'Registró movimiento SALIDA desde bodega ''Bodega Central'' - Cliente: Empresa ABC S.A.S.'),
(6, 'Movimiento',       4, 'INSERT', CURRENT_TIMESTAMP - INTERVAL '4 hours',       1, null, '{"id":4,"tipo":"ENTRADA","bodegaDestino":"Bodega Central"}', '127.0.0.1', 'Registró movimiento ENTRADA en bodega ''Bodega Central'' - Auriculares Bluetooth (x8 u.)'),
(7, 'AjusteInventario', 3, 'INSERT', CURRENT_TIMESTAMP - INTERVAL '3 hours',       5, null, '{"tipo":"VENCIMIENTO","diferencia":-1}', '127.0.0.1', 'Ajuste por VENCIMIENTO en Bodega Central para Impresora HP LaserJet'),
(8, 'OrdenCompra',      3, 'INSERT', CURRENT_TIMESTAMP - INTERVAL '3 hours',       7, null, '{"codigo":"OC-20260828-001","total":1800000}', '127.0.0.1', 'Generación de nueva orden de compra a Distribuidora Papelera Andina')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('auditoria', 'id'), COALESCE((SELECT MAX(id) FROM auditoria), 1));
