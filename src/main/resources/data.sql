-- ============================================================
-- LogiTrack S.A. - data.sql
-- Datos iniciales de prueba (PostgreSQL)
-- ============================================================

-- ---- USUARIOS (contraseña: 'admin123' y 'empleado123' - BCrypt) ----
INSERT INTO usuarios (id, nombre, apellido, email, password, rol, activo, created_at) VALUES
(1, 'Admin',    'Sistema',    'admin@logitrack.com',    '$2a$10$r//FnmN.GUtMhQtM23FUhOUBQOwQZjElEu/.LXN701rSgFGKQUHpm', 'ADMIN',    true, CURRENT_TIMESTAMP),
(2, 'Carlos',   'González',   'carlos@logitrack.com',   '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'EMPLEADO', true, CURRENT_TIMESTAMP),
(3, 'María',    'López',      'maria@logitrack.com',    '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'EMPLEADO', true, CURRENT_TIMESTAMP),
(4, 'Andrés',   'Martínez',   'andres@logitrack.com',   '$2a$10$3FYeHXiHS6HKQGdBIub3J.C9YxwFGHyv39RHotSIs59FowisZM0iG', 'EMPLEADO', true, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

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
INSERT INTO productos (id, nombre, categoria, stock, precio, descripcion, activo, created_at) VALUES
(1,  'Laptop Dell XPS 15',     'Electrónica',  50,  4500000.00, 'Laptop empresarial 15 pulgadas',    true, CURRENT_TIMESTAMP),
(2,  'Monitor LG 27"',         'Electrónica',  30,  900000.00,  'Monitor Full HD 27 pulgadas',       true, CURRENT_TIMESTAMP),
(3,  'Teclado Mecánico',       'Periféricos',  80,  250000.00,  'Teclado mecánico retroiluminado',   true, CURRENT_TIMESTAMP),
(4,  'Mouse Inalámbrico',      'Periféricos',  120, 85000.00,   'Mouse ergonómico inalámbrico',      true, CURRENT_TIMESTAMP),
(5,  'Silla Ergonómica',       'Mobiliario',   25,  1200000.00, 'Silla de oficina con soporte lumbar',true, CURRENT_TIMESTAMP),
(6,  'Escritorio Ejecutivo',   'Mobiliario',   15,  850000.00,  'Escritorio en L 180cm',             true, CURRENT_TIMESTAMP),
(7,  'Papel A4 500 hojas',     'Papelería',   200,  18000.00,   'Resma de papel bond',               true, CURRENT_TIMESTAMP),
(8,  'Bolígrafos x12',         'Papelería',   300,  12000.00,   'Caja de bolígrafos azules',         true, CURRENT_TIMESTAMP),
(9,  'Impresora HP LaserJet',  'Electrónica',   8,  1800000.00, 'Impresora láser monocromática',     true, CURRENT_TIMESTAMP),
(10, 'Auriculares Bluetooth',  'Periféricos',  45,  320000.00,  'Auriculares cancelación de ruido',  true, CURRENT_TIMESTAMP)
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
