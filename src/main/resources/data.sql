-- ============================================================
-- LogiTrack S.A. - data.sql
-- Datos iniciales de prueba (PostgreSQL)
-- ============================================================

-- ---- CATEGORÍAS ----
INSERT INTO categorias (id, nombre, descripcion, activo, created_at) VALUES
(1, 'Electrónica',  'Equipos tecnológicos, laptops, monitores, periféricos', true, CURRENT_TIMESTAMP),
(2, 'Periféricos',   'Teclados, mouses, auriculares y accesorios',            true, CURRENT_TIMESTAMP),
(3, 'Mobiliario',    'Muebles de oficina, sillas, escritorios',               true, CURRENT_TIMESTAMP),
(4, 'Papelería',     'Artículos de oficina y papelería',                      true, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('categorias', 'id'), COALESCE((SELECT MAX(id) FROM categorias), 1));

-- ---- USUARIOS (contraseña: 'admin123', 'empleado123' - BCrypt) ----
INSERT INTO usuarios (nombre, apellido, email, password, rol, activo, created_at) VALUES
('Admin',    'Sistema',    'admin@logitrack.com',    '$2a$10$r//FnmN.GUtMhQtM23FUhOUBQOwQZjElEu/.LXN701rSgFGKQUHpm', 'ADMIN',             true, CURRENT_TIMESTAMP),
('Carlos',   'González',   'carlos@logitrack.com',   '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'EMPLEADO',          true, CURRENT_TIMESTAMP),
('María',    'López',      'maria@logitrack.com',    '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'EMPLEADO',          true, CURRENT_TIMESTAMP),
('Andrés',   'Martínez',   'andres@logitrack.com',   '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'EMPLEADO',          true, CURRENT_TIMESTAMP),
('Laura',    'Pérez',      'laura@logitrack.com',    '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'SUPERVISOR',        true, CURRENT_TIMESTAMP),
('Sofía',    'Ramírez',    'sofia@logitrack.com',    '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'GERENTE_LOGISTICA', true, CURRENT_TIMESTAMP),
('Pedro',    'Sánchez',    'pedro@logitrack.com',    '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'JEFE_COMPRAS',      true, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;

SELECT setval(pg_get_serial_sequence('usuarios', 'id'), COALESCE((SELECT MAX(id) FROM usuarios), 1));

-- ---- BODEGAS ----
INSERT INTO bodegas (id, nombre, ubicacion, capacidad, encargado, activo, created_at) VALUES
(1, 'Bodega Central',    'Bogotá, Cundinamarca',   5000, 'Carlos González',  true, CURRENT_TIMESTAMP),
(2, 'Bodega Norte',      'Medellín, Antioquia',    3000, 'María López',       true, CURRENT_TIMESTAMP),
(3, 'Bodega Sur',        'Cali, Valle del Cauca',  4000, 'Andrés Martínez',   true, CURRENT_TIMESTAMP),
(4, 'Bodega Caribe',     'Barranquilla, Atlántico',2000, 'Pedro Ramírez',     true, CURRENT_TIMESTAMP),
(5, 'Bodega Oriente',    'Bucaramanga, Santander', 3500, 'Ana Torres',        true, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('bodegas', 'id'), COALESCE((SELECT MAX(id) FROM bodegas), 1));

-- ---- PRODUCTOS ----
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
(10, 'Auriculares Bluetooth',  2, 45,  10, 320000.00,  'Auriculares cancelación de ruido',  true, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('productos', 'id'), COALESCE((SELECT MAX(id) FROM productos), 1));

-- ---- INVENTARIO INICIAL POR BODEGA ----
INSERT INTO inventario_bodega (bodega_id, producto_id, stock_actual, ultima_actualizacion) VALUES
(1, 1, 20, CURRENT_TIMESTAMP), (1, 2, 10, CURRENT_TIMESTAMP), (1, 3, 30, CURRENT_TIMESTAMP), (1, 4, 50, CURRENT_TIMESTAMP), (1, 5, 10, CURRENT_TIMESTAMP),
(1, 6,  5, CURRENT_TIMESTAMP), (1, 7, 80, CURRENT_TIMESTAMP), (1, 8,100, CURRENT_TIMESTAMP), (1, 9,  3, CURRENT_TIMESTAMP), (1,10, 20, CURRENT_TIMESTAMP),
(2, 1, 15, CURRENT_TIMESTAMP), (2, 2,  8, CURRENT_TIMESTAMP), (2, 3, 20, CURRENT_TIMESTAMP), (2, 4, 35, CURRENT_TIMESTAMP), (2, 5,  8, CURRENT_TIMESTAMP),
(2, 7, 60, CURRENT_TIMESTAMP), (2, 8, 80, CURRENT_TIMESTAMP), (2,10, 15, CURRENT_TIMESTAMP),
(3, 1, 10, CURRENT_TIMESTAMP), (3, 2,  7, CURRENT_TIMESTAMP), (3, 4, 25, CURRENT_TIMESTAMP), (3, 6,  5, CURRENT_TIMESTAMP), (3, 7, 40, CURRENT_TIMESTAMP),
(3, 8, 80, CURRENT_TIMESTAMP), (3, 9,  3, CURRENT_TIMESTAMP), (3,10, 10, CURRENT_TIMESTAMP),
(4, 3, 15, CURRENT_TIMESTAMP), (4, 4, 15, CURRENT_TIMESTAMP), (4, 7, 30, CURRENT_TIMESTAMP), (4, 8, 60, CURRENT_TIMESTAMP),
(5, 1,  5, CURRENT_TIMESTAMP), (5, 2,  5, CURRENT_TIMESTAMP), (5, 3, 15, CURRENT_TIMESTAMP), (5, 7, 30, CURRENT_TIMESTAMP)
ON CONFLICT (bodega_id, producto_id) DO NOTHING;

-- Configuración del sistema
INSERT INTO configuracion (clave, valor, descripcion) VALUES
    ('UMBRAL_APROBACION', '50', 'Número de unidades a partir del cual una solicitud de transferencia requiere aprobación de ADMIN'),
    ('LIMITE_DIARIO_EMPLEADO', '150', 'Límite máximo de unidades que un empleado puede transferir automáticamente en un día'),
    ('HORAS_EXPIRACION_SOLICITUD', '48', 'Horas antes de que una solicitud PENDIENTE expire automáticamente'),
    ('STOCK_MINIMO_DEFAULT', '10', 'Stock mínimo por defecto para alertas de stock bajo')
ON CONFLICT (clave) DO NOTHING;

-- Proveedores
INSERT INTO proveedores (id, nombre, ruc, telefono, email, direccion, activo, created_at) VALUES
    (1, 'TechSupplies S.A.', '900123456-1', '+57 601 555 0001', 'ventas@techsupplies.co', 'Cra 15 # 93-47, Bogotá', true, CURRENT_TIMESTAMP),
    (2, 'OfficeWorld Ltda.', '800987654-2', '+57 604 555 0002', 'pedidos@officeworld.co', 'Cl 50 # 43-65, Medellín', true, CURRENT_TIMESTAMP),
    (3, 'Distribuidora Nacional', '700456789-3', '+57 602 555 0003', 'info@distnacional.co', 'Av 3N # 23-45, Cali', true, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('proveedores', 'id'), COALESCE((SELECT MAX(id) FROM proveedores), 1));

-- Clientes
INSERT INTO clientes (id, nombre, ruc, telefono, email, direccion, activo, created_at) VALUES
    (1, 'Empresa ABC S.A.S.', '900111222-1', '+57 601 444 0001', 'compras@empresaabc.co', 'Cl 72 # 10-35, Bogotá', true, CURRENT_TIMESTAMP),
    (2, 'Constructora XYZ', '800333444-2', '+57 604 444 0002', 'logistica@xyz.co', 'Cl 30 # 65-20, Medellín', true, CURRENT_TIMESTAMP),
    (3, 'Comercial del Pacífico', '700555666-3', '+57 602 444 0003', 'admin@compac.co', 'Cr 1 # 12-30, Cali', true, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('clientes', 'id'), COALESCE((SELECT MAX(id) FROM clientes), 1));
