-- ============================================================
-- LogiTrack S.A. - data-test.sql (H2 In-Memory)
-- ============================================================

-- CATEGORIAS
INSERT INTO categorias (id, nombre, descripcion, activo, created_at) VALUES
(1, 'Electrónica',  'Equipos tecnológicos, laptops, monitores, periféricos', true, CURRENT_TIMESTAMP),
(2, 'Periféricos',   'Teclados, mouses, auriculares y accesorios',            true, CURRENT_TIMESTAMP),
(3, 'Mobiliario',    'Muebles de oficina, sillas, escritorios',               true, CURRENT_TIMESTAMP),
(4, 'Papelería',     'Artículos de oficina y papelería',                      true, CURRENT_TIMESTAMP);

-- USUARIOS
INSERT INTO usuarios (id, nombre, apellido, email, password, rol, activo, created_at) VALUES
(1, 'Admin',    'Sistema',    'admin@logitrack.com',    '$2a$10$r//FnmN.GUtMhQtM23FUhOUBQOwQZjElEu/.LXN701rSgFGKQUHpm', 'ADMIN',             true, CURRENT_TIMESTAMP),
(2, 'Carlos',   'González',   'carlos@logitrack.com',   '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'EMPLEADO',          true, CURRENT_TIMESTAMP),
(3, 'María',    'López',      'maria@logitrack.com',    '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'EMPLEADO',          true, CURRENT_TIMESTAMP),
(4, 'Andrés',   'Martínez',   'andres@logitrack.com',   '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'EMPLEADO',          true, CURRENT_TIMESTAMP),
(5, 'Laura',    'Pérez',      'laura@logitrack.com',    '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'SUPERVISOR',        true, CURRENT_TIMESTAMP),
(6, 'Sofía',    'Ramírez',    'sofia@logitrack.com',    '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'GERENTE_LOGISTICA', true, CURRENT_TIMESTAMP),
(7, 'Pedro',    'Sánchez',    'pedro@logitrack.com',    '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'JEFE_COMPRAS',      true, CURRENT_TIMESTAMP);

-- BODEGAS
INSERT INTO bodegas (id, nombre, ubicacion, capacidad, encargado, activo, created_at) VALUES
(1, 'Bodega Central',    'Bogotá, Cundinamarca',   5000, 'Carlos González',  true, CURRENT_TIMESTAMP),
(2, 'Bodega Norte',      'Medellín, Antioquia',    3000, 'María López',       true, CURRENT_TIMESTAMP),
(3, 'Bodega Sur',        'Cali, Valle del Cauca',  4000, 'Andrés Martínez',   true, CURRENT_TIMESTAMP),
(4, 'Bodega Caribe',     'Barranquilla, Atlántico',2000, 'Pedro Ramírez',     true, CURRENT_TIMESTAMP),
(5, 'Bodega Oriente',    'Bucaramanga, Santander', 3500, 'Ana Torres',        true, CURRENT_TIMESTAMP);

-- PRODUCTOS
INSERT INTO productos (id, nombre, categoria_id, stock, stock_minimo, precio, descripcion, activo, created_at) VALUES
(1,  'Laptop Dell XPS 15',     1, 50,  10, 4500000.00, 'Laptop empresarial 15 pulgadas',    true, CURRENT_TIMESTAMP),
(2,  'Monitor LG 27"',         1, 30,  10, 900000.00,  'Monitor Full HD 27 pulgadas',       true, CURRENT_TIMESTAMP),
(3,  'Teclado Mecánico',       2, 80,  10, 250000.00,  'Teclado mecánico retroiluminado',   true, CURRENT_TIMESTAMP),
(4,  'Mouse Inalámbrico',      2, 125, 10, 85000.00,   'Mouse ergonómico inalámbrico',      true, CURRENT_TIMESTAMP),
(5,  'Silla Ergonómica',       3, 18,  10, 1200000.00, 'Silla de oficina con soporte lumbar',true, CURRENT_TIMESTAMP),
(6,  'Escritorio Ejecutivo',   3, 10,  10, 850000.00,  'Escritorio en L 180cm',             true, CURRENT_TIMESTAMP),
(7,  'Papel A4 500 hojas',     4, 240, 10, 18000.00,   'Resma de papel bond',               true, CURRENT_TIMESTAMP),
(8,  'Bolígrafos x12',         4, 320, 10, 12000.00,   'Caja de bolígrafos azules',         true, CURRENT_TIMESTAMP),
(9,  'Impresora HP LaserJet',  1, 6,   10, 1800000.00, 'Impresora láser monocromática',     true, CURRENT_TIMESTAMP),
(10, 'Disco SSD 1TB NVMe',     1, 8,   15, 380000.00,  'SSD ultrarrápido PCIe 4.0',         true, CURRENT_TIMESTAMP);

-- INVENTARIO BODEGA
INSERT INTO inventario_bodega (id, bodega_id, producto_id, stock_actual, ultima_actualizacion) VALUES
(1, 1, 1, 20, CURRENT_TIMESTAMP),
(2, 1, 2, 15, CURRENT_TIMESTAMP),
(3, 1, 3, 40, CURRENT_TIMESTAMP),
(4, 1, 9, 2,  CURRENT_TIMESTAMP),
(5, 1, 10, 3, CURRENT_TIMESTAMP),
(6, 2, 1, 15, CURRENT_TIMESTAMP),
(7, 2, 4, 60, CURRENT_TIMESTAMP),
(8, 3, 1, 15, CURRENT_TIMESTAMP),
(9, 3, 5, 10, CURRENT_TIMESTAMP);

-- PROVEEDORES
INSERT INTO proveedores (id, nombre, ruc, telefono, email, direccion, activo, created_at) VALUES
(1, 'Tech Supplies Colombia S.A.S.', '900123456-1', '601-5550100', 'contacto@techsupplies.co', 'Cra 15 # 93-47, Bogotá',       true, CURRENT_TIMESTAMP),
(2, 'Muebles & Diseños Corporativos', '900654321-2', '604-4440200', 'ventas@mueblescorp.co',    'Calle 10 # 43E-12, Medellín',  true, CURRENT_TIMESTAMP),
(3, 'Papelería Industrial del Valle', '900987654-3', '602-3330300', 'pedidos@papeleriavalle.co', 'Av 6N # 28N-15, Cali',         true, CURRENT_TIMESTAMP);

-- CLIENTES
INSERT INTO clientes (id, nombre, ruc, telefono, email, direccion, activo, created_at) VALUES
(1, 'Soluciones Digitales ABC S.A.S.', '901111222-1', '601-2220101', 'compras@solucionesabc.co', 'Calle 72 # 10-34, Bogotá',      true, CURRENT_TIMESTAMP),
(2, 'Inversiones Antioquia Ltda.',     '901333444-2', '604-3330202', 'admin@inversionesant.co',   'Cra 43A # 1S-50, Medellín',     true, CURRENT_TIMESTAMP),
(3, 'Comercializadora del Pacífico',   '901555666-3', '602-4440303', 'gerencia@compacifico.co',   'Calle 5 # 38-20, Cali',         true, CURRENT_TIMESTAMP);

-- TRANSPORTADORAS
INSERT INTO transportadoras (id, nombre, ruc_nit, telefono, email, contacto, tipo_servicio, activo, created_at) VALUES
(1, 'LogiExpress Colombia S.A.',   '900999888-1', '601-8881111', 'despachos@logiexpress.co', 'Fernando Vega', 'TERRESTRE',    true, CURRENT_TIMESTAMP),
(2, 'AeroCarga Nacional Express',  '900777666-2', '601-8882222', 'operaciones@aerocarga.co', 'Claudia Rivas', 'AEREO',        true, CURRENT_TIMESTAMP),
(3, 'Flota LogiTrack Distribución','900111000-3', '601-8883333', 'flota@logitrack.com',      'Mario Ruiz',    'FLOTA_PROPIA', true, CURRENT_TIMESTAMP);

-- UNIDADES DE MEDIDA
INSERT INTO unidades_medida (id, codigo, nombre, abreviatura, factor_conversion, activo, created_at) VALUES
(1, 'UND', 'Unidad Individual', 'u.',  1.0,  true, CURRENT_TIMESTAMP),
(2, 'CJ',  'Caja Estándar',     'cj.', 12.0, true, CURRENT_TIMESTAMP);

-- TIPOS UBICACION
INSERT INTO tipos_ubicacion (id, codigo, nombre, peso_maximo_kg, volumen_maximo_m3, descripcion, activo, created_at) VALUES
(1, 'RCK-PESADO', 'Rack Industrial Pesado', 2500.0, 5.0, 'Rack pesado', true, CURRENT_TIMESTAMP);

-- ZONAS BODEGA
INSERT INTO zonas_bodega (id, codigo, nombre, tipo_zona, bodega_id, temperatura_controlada, descripcion, activo, created_at) VALUES
(1, 'ZN-B1-REC', 'Zona Recepción', 'RECEPCION', 1, false, 'Recepción Bogotá', true, CURRENT_TIMESTAMP);

ALTER TABLE categorias ALTER COLUMN id RESTART WITH 100;
ALTER TABLE usuarios ALTER COLUMN id RESTART WITH 100;
ALTER TABLE bodegas ALTER COLUMN id RESTART WITH 100;
ALTER TABLE productos ALTER COLUMN id RESTART WITH 100;
ALTER TABLE inventario_bodega ALTER COLUMN id RESTART WITH 100;
ALTER TABLE proveedores ALTER COLUMN id RESTART WITH 100;
ALTER TABLE clientes ALTER COLUMN id RESTART WITH 100;
ALTER TABLE ajustes_inventario ALTER COLUMN id RESTART WITH 100;
ALTER TABLE ordenes_compra ALTER COLUMN id RESTART WITH 100;
ALTER TABLE ordenes_compra_detalles ALTER COLUMN id RESTART WITH 100;
ALTER TABLE lotes ALTER COLUMN id RESTART WITH 100;
ALTER TABLE ubicaciones_bodega ALTER COLUMN id RESTART WITH 100;
ALTER TABLE unidades_medida ALTER COLUMN id RESTART WITH 100;
ALTER TABLE producto_series ALTER COLUMN id RESTART WITH 100;
ALTER TABLE zonas_bodega ALTER COLUMN id RESTART WITH 100;
ALTER TABLE tipos_ubicacion ALTER COLUMN id RESTART WITH 100;
ALTER TABLE pedidos_cliente ALTER COLUMN id RESTART WITH 100;
ALTER TABLE pedido_cliente_detalles ALTER COLUMN id RESTART WITH 100;
ALTER TABLE transportadoras ALTER COLUMN id RESTART WITH 100;
ALTER TABLE guias_despacho ALTER COLUMN id RESTART WITH 100;
ALTER TABLE tareas_picking ALTER COLUMN id RESTART WITH 100;
ALTER TABLE conteos_ciclicos ALTER COLUMN id RESTART WITH 100;
ALTER TABLE conteo_ciclico_detalles ALTER COLUMN id RESTART WITH 100;



