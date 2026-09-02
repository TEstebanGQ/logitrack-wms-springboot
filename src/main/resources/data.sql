-- ============================================================
-- LogiTrack S.A. - data.sql
-- Datos iniciales y transaccionales consolidados (PostgreSQL)
-- 28 Tablas Integrales WMS / ERP con Trazabilidad Completa
-- ============================================================

-- ---- 1. CATEGORÍAS ----
INSERT INTO categorias (id, nombre, descripcion, activo, created_at) VALUES
(1, 'Electrónica & TI',       'Laptops, monitores, servidores, scanners y dispositivos TI', true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(2, 'Periféricos & Accesorios', 'Teclados, mouses, auriculares, cables y adaptadores',      true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(3, 'Mobiliario Ergonómico',  'Sillas ejecutivas, escritorios, estantes y mobiliario',     true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(4, 'Papelería & Empaque',    'Resmas A4, cajas, bolígrafos, cintas y embalaje',            true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(5, 'Insumos Industriales',   'Herramientas, palets, etiquetadoras y seguridad industrial', true, CURRENT_TIMESTAMP - INTERVAL '15 days')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('categorias', 'id'), COALESCE((SELECT MAX(id) FROM categorias), 1));

-- ---- 2. UNIDADES DE MEDIDA ----
INSERT INTO unidades_medida (id, codigo, nombre, abreviatura, factor_conversion, activo, created_at) VALUES
(1, 'UND', 'Unidad Individual', 'u.',  1.0,  true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(2, 'CJ',  'Caja Estándar x12', 'cj.', 12.0, true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(3, 'PLT', 'Palet Industrial',  'plt.',48.0, true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(4, 'KG',  'Kilogramo Net',     'kg',  1.0,  true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(5, 'PK',  'Paquete x6',        'pk.', 6.0,  true, CURRENT_TIMESTAMP - INTERVAL '15 days')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('unidades_medida', 'id'), COALESCE((SELECT MAX(id) FROM unidades_medida), 1));

-- ---- 3. USUARIOS Y PERSONAL (12 Cuentas con BCrypt 'admin123' / 'empleado123') ----
INSERT INTO usuarios (id, nombre, apellido, email, password, rol, activo, created_at) VALUES
(1,  'Admin',           'Sistema',            'admin@logitrack.com',                       '$2a$10$r//FnmN.GUtMhQtM23FUhOUBQOwQZjElEu/.LXN701rSgFGKQUHpm', 'ADMIN',             true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(2,  'Carlos',          'González',           'carlos@logitrack.com',                      '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'EMPLEADO',          true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(3,  'María',           'López',              'maria@logitrack.com',                       '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'EMPLEADO',          true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(4,  'Andrés',          'Martínez',           'andres@logitrack.com',                      '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'EMPLEADO',          true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(5,  'Laura',           'Pérez',              'laura@logitrack.com',                       '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'SUPERVISOR',        true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(6,  'Sofía',           'Ramírez',            'sofia@logitrack.com',                       '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'GERENTE_LOGISTICA', true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(7,  'Pedro',           'Sánchez',            'pedro@logitrack.com',                       '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'JEFE_COMPRAS',      true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(8,  'Tomás Esteban',   'González Quintero',  'tomasestebangonzalezquintero@gmail.com',    '$2a$10$r//FnmN.GUtMhQtM23FUhOUBQOwQZjElEu/.LXN701rSgFGKQUHpm', 'SUPER_ADMIN',       true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(9,  'Felipe',          'Restrepo',           'felipe@logitrack.com',                      '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'EMPLEADO',          true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(10, 'Ana',             'Torres',             'ana@logitrack.com',                         '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'SUPERVISOR',        true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(11, 'Roberto',         'Gómez',              'roberto@logitrack.com',                     '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'JEFE_COMPRAS',      true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(12, 'Diana',           'Morales',            'diana@logitrack.com',                       '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'GERENTE_LOGISTICA', true, CURRENT_TIMESTAMP - INTERVAL '15 days')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('usuarios', 'id'), COALESCE((SELECT MAX(id) FROM usuarios), 1));

-- ---- 4. BODEGAS (10 Bodegas de Cobertura Nacional) ----
INSERT INTO bodegas (id, nombre, ubicacion, capacidad, encargado, activo, created_at) VALUES
(1,  'Bodega Central Bogotá',            'Bogotá, Cundinamarca - Zona Industrial Fontibón', 10000, 'Carlos González', true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(2,  'Centro Logístico Medellín',        'Medellín, Antioquia - Guayabal Itagüí',            6000, 'María López',      true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(3,  'Almacén Pacífico Cali',            'Cali, Valle del Cauca - Acopi Yumbo',             7500, 'Andrés Martínez',  true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(4,  'Bodega Caribe Barranquilla',       'Barranquilla, Atlántico - Zona Franca Vía 40',     5000, 'Felipe Restrepo',  true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(5,  'Centro Distribución Bucaramanga',  'Bucaramanga, Santander - Chimitá Giron',          4500, 'Ana Torres',       true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(6,  'Bodega Eje Cafetero Pereira',      'Pereira, Risaralda - Zona Franca Dosquebradas',    4000, 'Laura Pérez',      true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(7,  'Almacén Franca Cartagena',         'Cartagena, Bolívar - Zona Franca Mamonal',         8000, 'Pedro Sánchez',    true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(8,  'Bodega Frontera Cúcuta',           'Cúcuta, Norte de Santander - Anillo Vial',        3500, 'Sofía Ramírez',    true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(9,  'Centro Logístico Tolima Ibagué',   'Ibagué, Tolima - Parque Industrial Picaleña',      3000, 'Roberto Gómez',    true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(10, 'Bodega Llanos Villavicencio',      'Villavicencio, Meta - Vía Puerto López',           3500, 'Diana Morales',    true, CURRENT_TIMESTAMP - INTERVAL '15 days')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('bodegas', 'id'), COALESCE((SELECT MAX(id) FROM bodegas), 1));

-- ---- 5. ZONAS DE BODEGA ----
INSERT INTO zonas_bodega (id, codigo, nombre, tipo_zona, bodega_id, temperatura_controlada, descripcion, activo, created_at) VALUES
(1, 'ZN-B1-REC', 'Zona Recepción y Muelles',     'RECEPCION',      1, false, 'Muelle de carga y desembarque Bogotá',     true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(2, 'ZN-B1-ALM', 'Almacenamiento Racks Altos',   'ALMACENAMIENTO', 1, false, 'Pasillos 1 al 5 para alta densidad',       true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(3, 'ZN-B1-PCK', 'Zona de Picking Rápido',       'PICKING',        1, false, 'Módulos a nivel del suelo para recolección',true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(4, 'ZN-B1-DSP', 'Embalaje y Despacho',          'DESPACHO',       1, false, 'Consolidación de guías y albaranes',       true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(5, 'ZN-B2-ALM', 'Almacén General Medellín',     'ALMACENAMIENTO', 2, false, 'Bodega principal distribución Guayabal',    true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(6, 'ZN-B3-ALM', 'Almacén General Cali',         'ALMACENAMIENTO', 3, false, 'Bodega principal Acopi Yumbo',            true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(7, 'ZN-B4-FRI', 'Zona Fría y Climatizada',      'ALMACENAMIENTO', 4, true,  'Control de temperatura 15°C para insumos',   true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(8, 'ZN-B7-MAR', 'Bodega Marítima Cartagena',    'ALMACENAMIENTO', 7, false, 'Zona Franca Mamonal contenedores',        true, CURRENT_TIMESTAMP - INTERVAL '15 days')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('zonas_bodega', 'id'), COALESCE((SELECT MAX(id) FROM zonas_bodega), 1));

-- ---- 6. TIPOS DE UBICACIÓN ----
INSERT INTO tipos_ubicacion (id, codigo, nombre, peso_maximo_kg, volumen_maximo_m3, descripcion, activo, created_at) VALUES
(1, 'RCK-PESADO', 'Rack Industrial Pesado', 2500.0, 5.0, 'Para pallets con servidores y maquinaria', true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(2, 'EST-MEDIA',  'Estantería Carga Media', 800.0,  2.0, 'Para laptops y periféricos en cajas', true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(3, 'PCK-LIV',    'Gaveta de Picking',      150.0,  0.5, 'Para mouses, cables y accesorios pequeños', true, CURRENT_TIMESTAMP - INTERVAL '15 days')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('tipos_ubicacion', 'id'), COALESCE((SELECT MAX(id) FROM tipos_ubicacion), 1));

-- ---- 7. UBICACIONES FÍSICAS DE BODEGA ----
INSERT INTO ubicaciones_bodega (id, codigo_ubicacion, pasillo, estante, nivel, capacidad_max, descripcion, bodega_id, activo, created_at) VALUES
(1, 'B1-PAS1-RACK-A1', 'Pasillo 1', 'Rack A', 'Nivel 1 (Bajo)',  200, 'Alta rotación Laptops Dell XPS',   1, true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(2, 'B1-PAS1-RACK-A2', 'Pasillo 1', 'Rack A', 'Nivel 2 (Medio)', 200, 'Almacenamiento Monitores LG 27"', 1, true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(3, 'B1-PAS2-RACK-B1', 'Pasillo 2', 'Rack B', 'Nivel 1 (Bajo)',  300, 'Sillas ergonómicas y escritoria', 1, true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(4, 'B1-PAS3-RACK-C1', 'Pasillo 3', 'Rack C', 'Nivel 1 (Bajo)',  500, 'Periféricos y papelería compacta', 1, true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(5, 'B2-PAS1-RACK-A1', 'Pasillo 1', 'Rack A', 'Nivel 1',         150, 'Recepción Medellín Guayabal',      2, true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(6, 'B3-PAS1-RACK-A1', 'Pasillo 1', 'Rack A', 'Nivel 1',         200, 'Recepción Cali Acopi Yumbo',        3, true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(7, 'B4-PAS1-RACK-A1', 'Pasillo 1', 'Rack A', 'Nivel 1',         180, 'Zona Franca Barranquilla',         4, true, CURRENT_TIMESTAMP - INTERVAL '10 days'),
(8, 'B7-PAS1-RACK-A1', 'Pasillo 1', 'Rack A', 'Nivel 1',         250, 'Zona Franca Mamonal Cartagena',    7, true, CURRENT_TIMESTAMP - INTERVAL '10 days')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('ubicaciones_bodega', 'id'), COALESCE((SELECT MAX(id) FROM ubicaciones_bodega), 1));

-- ---- 8. PRODUCTOS (Catálogo Completo de 15 Ítems) ----
INSERT INTO productos (id, nombre, categoria_id, stock, stock_minimo, precio, descripcion, activo, created_at) VALUES
(1,  'Laptop Dell XPS 15 Pro',       1, 75,  10, 4500000.00, 'Laptop empresarial Core i7 16GB 512GB SSD', true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(2,  'Monitor LG 27" 4K IPS',        1, 45,  10, 900000.00,  'Monitor UltraFine UHD 4K HDMI y DisplayPort', true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(3,  'Teclado Mecánico RGB',         2, 115, 15, 250000.00,  'Teclado mecánico retroiluminado Switch Red',  true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(4,  'Mouse Inalámbrico Ergo',       2, 185, 20, 85000.00,   'Mouse ergonómico receptor USB y Bluetooth',   true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(5,  'Silla Ergonómica 3D',          3, 35,  5,  1200000.00, 'Silla ejecutiva malla transpirable 3D',       true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(6,  'Escritorio Ejecutivo L 180cm', 3, 20,  5,  850000.00,  'Escritorio en L estructura acero pulido',     true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(7,  'Papel A4 500H Resma Premium',  4, 450, 50, 18000.00,   'Resma papel bond 75g de alta blancura',       true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(8,  'Bolígrafos Micropunta x12',    4, 520, 40, 12000.00,   'Caja de 12 bolígrafos precisión negra/azul',  true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(9,  'Impresora HP LaserJet Pro',    1, 12,  10, 1800000.00, 'Impresora láser dúplex alta velocidad',       true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(10, 'Auriculares Bluetooth ANC',    2, 65,  10, 320000.00,  'Auriculares inalámbricos cancelación de ruido',true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(11, 'Scanner Códigos Honeywell',    1, 28,  5,  450000.00,  'Scanner industrial 2D omnidireccional USB',   true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(12, 'Cinta Embalaje Industrial x6', 4, 180, 20, 35000.00,   'Pack de 6 rollos cinta alta resistencia',    true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(13, 'Estante Metálico 5 Niveles',   3, 15,  3,  650000.00,  'Estantería modular metálica carga 400kg',     true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(14, 'Palet Plástico Pesado',        5, 60,  10, 180000.00,  'Palet plástico reforzado antiderrames 1x1.2m', true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(15, 'Etiquetadora Térmica Zebra',   1, 18,  4,  950000.00,  'Impresora de etiquetas código de barras 4"',  true, CURRENT_TIMESTAMP - INTERVAL '15 days')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('productos', 'id'), COALESCE((SELECT MAX(id) FROM productos), 1));

-- ---- 9. NÚMEROS DE SERIE (Trazabilidad Unitaria) ----
INSERT INTO producto_series (id, numero_serie, producto_id, bodega_id, ubicacion_id, estado, fecha_ingreso, fecha_despacho, observaciones) VALUES
(1,  'SN-DELL-XPS-001', 1, 1, 1, 'EN_STOCK',   CURRENT_TIMESTAMP - INTERVAL '3 days', null, 'Ingresado por lote inicial'),
(2,  'SN-DELL-XPS-002', 1, 1, 1, 'EN_STOCK',   CURRENT_TIMESTAMP - INTERVAL '3 days', null, 'Ingresado por lote inicial'),
(3,  'SN-DELL-XPS-003', 1, 1, 1, 'DESPACHADO', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '1 day', 'Despachado en pedido PED-20260827-001'),
(4,  'SN-LG-MON-001',   2, 1, 2, 'EN_STOCK',   CURRENT_TIMESTAMP - INTERVAL '2 days', null, 'Verificado en control de calidad'),
(5,  'SN-LG-MON-002',   2, 2, 5, 'EN_STOCK',   CURRENT_TIMESTAMP - INTERVAL '2 days', null, 'Trasladado a bodega Medellín'),
(6,  'SN-ZEBRA-001',   15, 1, 4, 'EN_STOCK',   CURRENT_TIMESTAMP - INTERVAL '1 day',  null, 'Etiquetadora lista para producción'),
(7,  'SN-HONEY-001',   11, 3, 6, 'EN_STOCK',   CURRENT_TIMESTAMP - INTERVAL '1 day',  null, 'Scanner asignado a bodega Cali')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('producto_series', 'id'), COALESCE((SELECT MAX(id) FROM producto_series), 1));

-- ---- 10. INVENTARIO POR BODEGA (Saldos Consistentes en las 10 Bodegas - 100% Reconciliado) ----
INSERT INTO inventario_bodega (bodega_id, producto_id, stock_actual, ultima_actualizacion) VALUES
(1, 1, 25, CURRENT_TIMESTAMP), (1, 2, 12, CURRENT_TIMESTAMP), (1, 3, 35, CURRENT_TIMESTAMP), (1, 4, 55, CURRENT_TIMESTAMP), (1, 5, 12, CURRENT_TIMESTAMP),
(1, 6,  7, CURRENT_TIMESTAMP), (1, 7, 90, CURRENT_TIMESTAMP), (1, 8,110, CURRENT_TIMESTAMP), (1, 9,  4, CURRENT_TIMESTAMP), (1,10, 22, CURRENT_TIMESTAMP),
(1,11, 10, CURRENT_TIMESTAMP), (1,12, 50, CURRENT_TIMESTAMP), (1,13, 10, CURRENT_TIMESTAMP), (1,14, 15, CURRENT_TIMESTAMP), (1,15, 10, CURRENT_TIMESTAMP),
(2, 1, 15, CURRENT_TIMESTAMP), (2, 2,  8, CURRENT_TIMESTAMP), (2, 3, 20, CURRENT_TIMESTAMP), (2, 4, 35, CURRENT_TIMESTAMP), (2, 5,  8, CURRENT_TIMESTAMP),
(2, 6,  8, CURRENT_TIMESTAMP), (2, 7, 60, CURRENT_TIMESTAMP), (2, 8, 80, CURRENT_TIMESTAMP), (2, 9,  4, CURRENT_TIMESTAMP), (2,10, 15, CURRENT_TIMESTAMP),
(2,11,  8, CURRENT_TIMESTAMP), (2,12, 40, CURRENT_TIMESTAMP), (2,13,  5, CURRENT_TIMESTAMP), (2,15,  8, CURRENT_TIMESTAMP),
(3, 1, 10, CURRENT_TIMESTAMP), (3, 2,  7, CURRENT_TIMESTAMP), (3, 3, 15, CURRENT_TIMESTAMP), (3, 4, 25, CURRENT_TIMESTAMP), (3, 5, 10, CURRENT_TIMESTAMP),
(3, 6,  5, CURRENT_TIMESTAMP), (3, 7, 40, CURRENT_TIMESTAMP), (3, 8, 80, CURRENT_TIMESTAMP), (3, 9,  4, CURRENT_TIMESTAMP), (3,10, 11, CURRENT_TIMESTAMP),
(3,11, 10, CURRENT_TIMESTAMP), (3,14, 20, CURRENT_TIMESTAMP),
(4, 1,  8, CURRENT_TIMESTAMP), (4, 3, 15, CURRENT_TIMESTAMP), (4, 4, 15, CURRENT_TIMESTAMP), (4, 7, 50, CURRENT_TIMESTAMP), (4, 8, 60, CURRENT_TIMESTAMP),
(4,10, 17, CURRENT_TIMESTAMP), (4,12, 40, CURRENT_TIMESTAMP),
(5, 1,  5, CURRENT_TIMESTAMP), (5, 2,  5, CURRENT_TIMESTAMP), (5, 3, 15, CURRENT_TIMESTAMP), (5, 4, 20, CURRENT_TIMESTAMP), (5, 7, 40, CURRENT_TIMESTAMP), (5, 8, 40, CURRENT_TIMESTAMP),
(6, 1,  4, CURRENT_TIMESTAMP), (6, 2,  4, CURRENT_TIMESTAMP), (6, 4, 15, CURRENT_TIMESTAMP), (6, 5,  5, CURRENT_TIMESTAMP), (6, 7, 30, CURRENT_TIMESTAMP), (6, 8, 30, CURRENT_TIMESTAMP),
(7, 1,  3, CURRENT_TIMESTAMP), (7, 2,  4, CURRENT_TIMESTAMP), (7, 7, 50, CURRENT_TIMESTAMP), (7, 8, 10, CURRENT_TIMESTAMP), (7,12, 50, CURRENT_TIMESTAMP), (7,14, 25, CURRENT_TIMESTAMP),
(8, 2,  3, CURRENT_TIMESTAMP), (8, 4, 20, CURRENT_TIMESTAMP), (8, 7, 30, CURRENT_TIMESTAMP), (8, 8, 40, CURRENT_TIMESTAMP),
(9, 1,  3, CURRENT_TIMESTAMP), (9, 2,  2, CURRENT_TIMESTAMP), (9, 7, 30, CURRENT_TIMESTAMP), (9, 8, 30, CURRENT_TIMESTAMP),
(10,1,  2, CURRENT_TIMESTAMP), (10,3, 15, CURRENT_TIMESTAMP), (10,7, 30, CURRENT_TIMESTAMP), (10,8, 40, CURRENT_TIMESTAMP)
ON CONFLICT (bodega_id, producto_id) DO UPDATE SET stock_actual = EXCLUDED.stock_actual, ultima_actualizacion = CURRENT_TIMESTAMP;

-- ---- 11. PROVEEDORES (Directorio Completo) ----
INSERT INTO proveedores (id, nombre, ruc, telefono, email, direccion, activo, created_at) VALUES
(1, 'TechSupplies Colombia S.A.S.',  '900123456-1', '+57 601 555 0001', 'ventas@techsupplies.co',    'Cra 15 # 93-47 Oficina 502, Bogotá',  true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(2, 'OfficeWorld & Muebles Ltda.',   '800987654-2', '+57 604 555 0002', 'pedidos@officeworld.co',   'Cl 50 # 43-65, Medellín',             true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(3, 'Distribuidora Papelera Andina', '700456789-3', '+57 602 555 0003', 'contacto@papelandina.co',   'Av 3N # 23-45, Cali',                 true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(4, 'Logística & Periféricos Global','901222333-4', '+57 605 555 0004', 'ventas@globalperif.com',    'Vía 40 # 73-290, Barranquilla',       true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(5, 'Industrias Metalmecánicas Eje', '890333222-5', '+57 606 555 0005', 'comercial@metalicoeje.co', 'Km 5 Vía Armenia, Pereira',         true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(6, 'Empaques & Soluciones Mamonal', '900888777-6', '+57 605 555 0006', 'ventas@empaquesmamonal.co', 'Zona Franca Mamonal Lote 4, Cartagena',true, CURRENT_TIMESTAMP - INTERVAL '15 days')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('proveedores', 'id'), COALESCE((SELECT MAX(id) FROM proveedores), 1));

-- ---- 12. CLIENTES ----
INSERT INTO clientes (id, nombre, ruc, telefono, email, direccion, activo, created_at) VALUES
(1, 'Soluciones Digitales ABC S.A.S.', '900111222-1', '+57 601 444 0001', 'compras@empresaabc.co',   'Cl 72 # 10-35, Bogotá',               true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(2, 'Constructora & Diseños XYZ',      '800333444-2', '+57 604 444 0002', 'logistica@xyzcorp.co',    'Cl 30 # 65-20, Medellín',             true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(3, 'Comercializadora del Pacífico',   '700555666-3', '+57 602 444 0003', 'admin@compacifico.co',    'Cr 1 # 12-30, Cali',                  true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(4, 'Inversiones Tecnológicas Caribe', '901777888-4', '+57 605 444 0004', 'adquisiciones@tecari.com', 'Calle 84 # 51B-32, Barranquilla',     true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(5, 'Grupo Empresarial del Santander', '890222111-5', '+57 607 444 0005', 'compras@gruposantander.co', 'Carrera 27 # 36-14, Bucaramanga',   true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(6, 'Almacenes El Cafetal S.A.',       '800666555-6', '+57 606 444 0006', 'suministros@elcafetal.co',  'Avenida 30 de Agosto # 45-12, Pereira',true, CURRENT_TIMESTAMP - INTERVAL '15 days')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('clientes', 'id'), COALESCE((SELECT MAX(id) FROM clientes), 1));

-- ---- 13. TRANSPORTADORAS ----
INSERT INTO transportadoras (id, nombre, ruc_nit, telefono, email, contacto, tipo_servicio, activo, created_at) VALUES
(1, 'LogiExpress Colombia S.A.',   '900999888-1', '+57 601 888 1111', 'despachos@logiexpress.co', 'Fernando Vega', 'TERRESTRE',    true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(2, 'AeroCarga Nacional Express',  '900777666-2', '+57 601 888 2222', 'operaciones@aerocarga.co', 'Claudia Rivas', 'AEREO',        true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(3, 'Flota LogiTrack Distribución','900111000-3', '+57 601 888 3333', 'flota@logitrack.com',      'Mario Ruiz',    'FLOTA_PROPIA', true, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(4, 'Transportes del Caribe Ltda.', '900444555-4', '+57 605 888 4444', 'servicio@transcaribe.co',  'Jorge Mendoza', 'TERRESTRE',    true, CURRENT_TIMESTAMP - INTERVAL '15 days')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('transportadoras', 'id'), COALESCE((SELECT MAX(id) FROM transportadoras), 1));

-- ---- 14. PEDIDOS DE CLIENTES ----
INSERT INTO pedidos_cliente (id, codigo_pedido, cliente_id, bodega_origen_id, estado, total_pedido, fecha_pedido, fecha_compromiso, direccion_entrega, observaciones, usuario_creador_id) VALUES
(1, 'PED-20260827-001', 1, 1, 'DESPACHADO',     9000000.00, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_DATE + INTERVAL '2 days', 'Cl 72 # 10-35, Bogotá',       'Entrega prioritaria laptops', 1),
(2, 'PED-20260828-001', 2, 2, 'EN_PREPARACION',  425000.00, CURRENT_TIMESTAMP - INTERVAL '4 hours',CURRENT_DATE + INTERVAL '3 days', 'Cl 30 # 65-20, Medellín',     'Despacho de mouses ergonómicos', 1),
(3, 'PED-20260828-002', 3, 1, 'PENDIENTE',      1800000.00, CURRENT_TIMESTAMP - INTERVAL '2 hours',CURRENT_DATE + INTERVAL '4 days', 'Cr 1 # 12-30, Cali',          'Solicitud de resmas de papel', 1),
(4, 'PED-20260828-003', 4, 4, 'PENDIENTE',      3600000.00, CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_DATE + INTERVAL '3 days', 'Calle 84 # 51B-32, Barranquilla', 'Pedido monitores LG 4K', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO pedido_cliente_detalles (id, pedido_id, producto_id, cantidad_solicitada, cantidad_despachada, precio_unitario, subtotal) VALUES
(1, 1, 1, 2, 2, 4500000.00, 9000000.00),
(2, 2, 4, 5, 0,   85000.00,  425000.00),
(3, 3, 7, 100, 0,  18000.00, 1800000.00),
(4, 4, 2, 4, 0,  900000.00, 3600000.00)
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
(2, 'PCK-2-1', 2, 4, 4, 3, 5, 3, 'EN_PROCESO',  CURRENT_TIMESTAMP - INTERVAL '3 hours',       null, 'Recolección parcial de 3 mouses'),
(3, 'PCK-3-1', 3, 7, 4, 4, 100, 0, 'PENDIENTE', CURRENT_TIMESTAMP - INTERVAL '1 hour',        null, 'Pendiente inicio en pasillo 3')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('tareas_picking', 'id'), COALESCE((SELECT MAX(id) FROM tareas_picking), 1));

-- ---- 17. CONTEOS CÍCLICOS Y AUDITORÍA FÍSICA ----
INSERT INTO conteos_ciclicos (id, codigo_conteo, bodega_id, zona_id, fecha_programada, fecha_ejecucion, estado, supervisor_id, observaciones) VALUES
(1, 'AUD-20260827-01', 1, 2, CURRENT_DATE - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 day 2 hours', 'CERRADO',    5, 'Auditoría de alta rotación pasillo 1 completada'),
(2, 'AUD-20260828-01', 1, 3, CURRENT_DATE,                     CURRENT_TIMESTAMP - INTERVAL '2 hours',        'EN_PROCESO', 5, 'Conteo de estantería de periféricos')
ON CONFLICT (id) DO NOTHING;

INSERT INTO conteo_ciclico_detalles (id, conteo_id, producto_id, ubicacion_id, stock_sistema, stock_fisico, diferencia, estado_linea, notas) VALUES
(1, 1, 1, 1, 25, 25,  0, 'CONCILIADO', 'Stock conforme en pasillo 1'),
(2, 1, 2, 2, 12, 12,  0, 'CONCILIADO', 'Stock conforme en estante A2'),
(3, 2, 4, 4, 55, 54, -1, 'RECONTAR',   'Posible unidad extraviada en picking')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('conteos_ciclicos', 'id'), COALESCE((SELECT MAX(id) FROM conteos_ciclicos), 1));
SELECT setval(pg_get_serial_sequence('conteo_ciclico_detalles', 'id'), COALESCE((SELECT MAX(id) FROM conteo_ciclico_detalles), 1));

-- ---- 18. LOTES Y TRAZABILIDAD FEFO (12 Lotes Consistentes con Producto y Bodega) ----
INSERT INTO lotes (id, codigo_lote, producto_id, bodega_id, stock_inicial, stock_actual, fecha_fabricacion, fecha_vencimiento, estado, created_at) VALUES
(1,  'LOT-DELL-2026-01', 1,  1, 30,  25,  CURRENT_DATE - INTERVAL '30 days', CURRENT_DATE + INTERVAL '365 days', 'DISPONIBLE',       CURRENT_TIMESTAMP - INTERVAL '2 days'),
(2,  'LOT-LG-2026-01',   2,  1, 15,  12,  CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE + INTERVAL '500 days', 'DISPONIBLE',       CURRENT_TIMESTAMP - INTERVAL '2 days'),
(3,  'LOT-TEC-2026-02',  3,  1, 40,  35,  CURRENT_DATE - INTERVAL '15 days', CURRENT_DATE + INTERVAL '400 days', 'DISPONIBLE',       CURRENT_TIMESTAMP - INTERVAL '1 day'),
(4,  'LOT-PAPEL-2026-A', 7,  1, 100, 90,  CURRENT_DATE - INTERVAL '40 days', CURRENT_DATE + INTERVAL '15 days',  'DISPONIBLE',       CURRENT_TIMESTAMP - INTERVAL '1 day'),
(5,  'LOT-IMP-2026-01',  9,  1, 5,   4,   CURRENT_DATE - INTERVAL '60 days', CURRENT_DATE - INTERVAL '5 days',   'VENCIDO',          CURRENT_TIMESTAMP - INTERVAL '1 day'),
(6,  'LOT-SILA-2026-01', 5,  1, 15,  12,  CURRENT_DATE - INTERVAL '10 days', CURRENT_DATE + INTERVAL '600 days', 'DISPONIBLE',       CURRENT_TIMESTAMP - INTERVAL '12 hours'),
(7,  'LOT-HONEY-2026-1', 11, 2, 10,  8,   CURRENT_DATE - INTERVAL '25 days', CURRENT_DATE + INTERVAL '450 days', 'DISPONIBLE',       CURRENT_TIMESTAMP - INTERVAL '10 hours'),
(8,  'LOT-ZEBRA-2026-1', 15, 1, 10,  8,   CURRENT_DATE - INTERVAL '15 days', CURRENT_DATE + INTERVAL '300 days', 'DISPONIBLE',       CURRENT_TIMESTAMP - INTERVAL '8 hours'),
(9,  'LOT-CINTA-2026-1', 12, 2, 50,  40,  CURRENT_DATE - INTERVAL '45 days', CURRENT_DATE + INTERVAL '180 days', 'DISPONIBLE',       CURRENT_TIMESTAMP - INTERVAL '6 hours'),
(10, 'LOT-PALET-2026-1', 14, 3, 25,  20,  CURRENT_DATE - INTERVAL '50 days', CURRENT_DATE + INTERVAL '900 days', 'DISPONIBLE',       CURRENT_TIMESTAMP - INTERVAL '4 hours'),
(11, 'LOT-MED-DELL-01', 1,  2, 20,  15,  CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE + INTERVAL '340 days', 'DISPONIBLE',       CURRENT_TIMESTAMP - INTERVAL '3 hours'),
(12, 'LOT-CALI-MON-01', 2,  3, 10,  7,   CURRENT_DATE - INTERVAL '18 days', CURRENT_DATE + INTERVAL '480 days', 'DISPONIBLE',       CURRENT_TIMESTAMP - INTERVAL '2 hours')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('lotes', 'id'), COALESCE((SELECT MAX(id) FROM lotes), 1));

-- ---- 19. ÓRDENES DE COMPRA ----
INSERT INTO ordenes_compra (id, codigo_orden, proveedor_id, bodega_destino_id, estado, total_estimado, fecha_solicitud, fecha_entrega_esperada, observaciones, usuario_solicitante_id) VALUES
(1, 'OC-20260827-001', 1, 1, 'RECIBIDA',  22500000.00, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_DATE + INTERVAL '5 days', 'Pedido urgente Laptops Dell XPS reposición', 7),
(2, 'OC-20260827-002', 2, 1, 'APROBADA',   6000000.00, CURRENT_TIMESTAMP - INTERVAL '1 day',  CURRENT_DATE + INTERVAL '7 days', 'Reabastecimiento de mobiliario corporativo',     7),
(3, 'OC-20260828-001', 3, 1, 'PENDIENTE',  1800000.00, CURRENT_TIMESTAMP - INTERVAL '4 hours',CURRENT_DATE + INTERVAL '3 days', 'Pedido mensual de resmas y papelería',            7),
(4, 'OC-20260828-002', 1, 2, 'PENDIENTE',  4500000.00, CURRENT_TIMESTAMP - INTERVAL '2 hours',CURRENT_DATE + INTERVAL '4 days', 'Monitores adicionales para sede Medellín',         7)
ON CONFLICT (id) DO NOTHING;

INSERT INTO ordenes_compra_detalles (id, orden_compra_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
(1, 1, 1, 5, 4500000.00, 22500000.00),
(2, 2, 5, 5, 1200000.00,  6000000.00),
(3, 3, 7, 100, 18000.00,  1800000.00),
(4, 4, 2, 5, 900000.00,   4500000.00)
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('ordenes_compra', 'id'), COALESCE((SELECT MAX(id) FROM ordenes_compra), 1));
SELECT setval(pg_get_serial_sequence('ordenes_compra_detalles', 'id'), COALESCE((SELECT MAX(id) FROM ordenes_compra_detalles), 1));

-- ---- 20. MOVIMIENTOS DE INVENTARIO ----
INSERT INTO movimientos (id, tipo_movimiento, fecha, observaciones, usuario_id, bodega_origen_id, bodega_destino_id, proveedor_id, cliente_id) VALUES
(1, 'ENTRADA',       CURRENT_TIMESTAMP - INTERVAL '2 days',  'Ingreso de mercancía por OC-20260827-001', 1, null, 1, 1, null),
(2, 'SALIDA',        CURRENT_TIMESTAMP - INTERVAL '1 day',   'Despacho comercial pedido corporativo ABC', 2, 1, null, null, 1),
(3, 'TRANSFERENCIA', CURRENT_TIMESTAMP - INTERVAL '12 hours','Reubicación de stock de Bogotá a Medellín', 5, 1, 2, null, null),
(4, 'ENTRADA',       CURRENT_TIMESTAMP - INTERVAL '6 hours', 'Recepción de periféricos y auriculares',    1, null, 1, 4, null),
(5, 'SALIDA',        CURRENT_TIMESTAMP - INTERVAL '3 hours', 'Despacho cliente Constructora XYZ',         2, 2, null, null, 2),
(6, 'TRANSFERENCIA', CURRENT_TIMESTAMP - INTERVAL '1 hour',  'Envío de seguridad de stock a Cali',        5, 1, 3, null, null)
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

-- ---- 21. AJUSTES DE INVENTARIO Y MERMAS ----
INSERT INTO ajustes_inventario (id, bodega_id, producto_id, tipo_ajuste, cantidad_anterior, cantidad_nueva, diferencia, justificacion, usuario_id, fecha) VALUES
(1, 1, 4, 'DANO',          57, 55, -2, 'Mouse con cable deteriorado en transporte interno', 1, CURRENT_TIMESTAMP - INTERVAL '1 day'),
(2, 1, 7, 'CONTEO_FISICO', 88, 90, +2, 'Sobrante identificado en auditoría de estantería C', 5, CURRENT_TIMESTAMP - INTERVAL '18 hours'),
(3, 1, 9, 'VENCIMIENTO',    5,  4, -1, 'Tóner de prueba de fábrica vencido dado de baja',   5, CURRENT_TIMESTAMP - INTERVAL '6 hours'),
(4, 2, 3, 'MERMA',         21, 20, -1, 'Avería de switch en teclado durante picking',       1, CURRENT_TIMESTAMP - INTERVAL '2 hours')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('ajustes_inventario', 'id'), COALESCE((SELECT MAX(id) FROM ajustes_inventario), 1));

-- ---- 22. ALERTAS DE STOCK ----
INSERT INTO alertas_stock (id, producto_id, bodega_id, stock_actual, stock_minimo, estado, fecha_generada, fecha_resuelta, resuelta_por_id) VALUES
(1, 9, 1, 4, 10, 'PENDIENTE', CURRENT_TIMESTAMP - INTERVAL '1 day', null, null),
(2, 6, 3, 5, 10, 'PENDIENTE', CURRENT_TIMESTAMP - INTERVAL '8 hours', null, null),
(3, 1, 5, 5, 10, 'RESUELTA',  CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 day', 1)
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('alertas_stock', 'id'), COALESCE((SELECT MAX(id) FROM alertas_stock), 1));

-- ---- 23. NOTIFICACIONES ----
INSERT INTO notificaciones (id, usuario_id, titulo, mensaje, leida, tipo, fecha_creacion) VALUES
(1, 1, '⚠️ Alerta de Bajo Stock: Impresora HP', 'El stock de Impresora HP LaserJet en Bodega Central bajó a 4 unidades (Mín: 10).', false, 'ALERTA', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(2, 6, '📦 Orden de Compra Aprobada', 'Se aprobó la orden OC-20260827-002 para OfficeWorld por $6,000,000.', false, 'INFO', CURRENT_TIMESTAMP - INTERVAL '18 hours'),
(3, 1, '✓ Recepción Exitosa de Mercancía', 'La orden OC-20260827-001 fue recibida e ingresada al inventario de Bodega Central.', true, 'EXITO', CURRENT_TIMESTAMP - INTERVAL '2 days'),
(4, 7, '📝 Nueva Solicitud de Compra', 'Se ha emitido la orden OC-20260828-001 pendiente de autorización.', false, 'INFO', CURRENT_TIMESTAMP - INTERVAL '4 hours'),
(5, 5, '⚠️ Alerta de Bajo Stock: Escritorio Ejecutivo', 'Bodega Sur tiene 5 escritorios restantes (Mín: 10).', false, 'ALERTA', CURRENT_TIMESTAMP - INTERVAL '8 hours')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('notificaciones', 'id'), COALESCE((SELECT MAX(id) FROM notificaciones), 1));

-- ---- 24. AUDITORÍA AUTOMÁTICA ----
INSERT INTO auditoria (id, entidad, entidad_id, tipo_operacion, fecha_hora, usuario_id, valores_anteriores, valores_nuevos, ip_address, descripcion) VALUES
(1, 'Movimiento',       1, 'INSERT', CURRENT_TIMESTAMP - INTERVAL '2 days',  1, null, '{"id":1,"tipo":"ENTRADA","bodegaDestino":"Bodega Central Bogotá"}', '127.0.0.1', 'Registró movimiento ENTRADA en bodega ''Bodega Central Bogotá'' - Productos: Laptop Dell XPS (x5 u.)'),
(2, 'Producto',         1, 'UPDATE', CURRENT_TIMESTAMP - INTERVAL '2 days',  1, '{"stock":70}', '{"stock":75}', '127.0.0.1', 'Actualización de stock por recepción de mercancía'),
(3, 'OrdenCompra',      1, 'UPDATE', CURRENT_TIMESTAMP - INTERVAL '2 days',  1, '{"estado":"PENDIENTE"}', '{"estado":"RECIBIDA"}', '127.0.0.1', 'Recepción de orden de compra OC-20260827-001'),
(4, 'AjusteInventario', 1, 'INSERT', CURRENT_TIMESTAMP - INTERVAL '1 day',   1, null, '{"tipo":"DANO","diferencia":-2}', '127.0.0.1', 'Ajuste por DANO en Bodega Central Bogotá para Mouse Inalámbrico (Antes: 57, Nuevo: 55)'),
(5, 'Movimiento',       2, 'INSERT', CURRENT_TIMESTAMP - INTERVAL '1 day',   2, null, '{"id":2,"tipo":"SALIDA","bodegaOrigen":"Bodega Central Bogotá"}', '127.0.0.1', 'Registró movimiento SALIDA desde bodega ''Bodega Central Bogotá'' - Cliente: Empresa ABC S.A.S.'),
(6, 'Movimiento',       4, 'INSERT', CURRENT_TIMESTAMP - INTERVAL '6 hours', 1, null, '{"id":4,"tipo":"ENTRADA","bodegaDestino":"Bodega Central Bogotá"}', '127.0.0.1', 'Registró movimiento ENTRADA en bodega ''Bodega Central Bogotá'' - Auriculares Bluetooth (x8 u.)'),
(7, 'AjusteInventario', 3, 'INSERT', CURRENT_TIMESTAMP - INTERVAL '6 hours', 5, null, '{"tipo":"VENCIMIENTO","diferencia":-1}', '127.0.0.1', 'Ajuste por VENCIMIENTO en Bodega Central Bogotá para Impresora HP LaserJet Pro'),
(8, 'OrdenCompra',      3, 'INSERT', CURRENT_TIMESTAMP - INTERVAL '4 hours', 7, null, '{"codigo":"OC-20260828-001","total":1800000}', '127.0.0.1', 'Generación de nueva orden de compra a Distribuidora Papelera Andina')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('auditoria', 'id'), COALESCE((SELECT MAX(id) FROM auditoria), 1));
