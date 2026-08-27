-- ============================================================
-- LogiTrack S.A. - data.sql
-- Datos iniciales de prueba
-- ============================================================

USE logitrack_db;

-- ---- USUARIOS (contraseña: 'admin123' y 'empleado123' - BCrypt) ----
INSERT IGNORE INTO usuarios (nombre, apellido, email, password, rol, activo) VALUES
('Admin',    'Sistema',    'admin@logitrack.com',    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyMCdTMGy', 'ADMIN',    true),
('Carlos',   'González',   'carlos@logitrack.com',   '$2a$10$5YZqxHRVXc1ANKbXA6t4Ne8YR/4V6G4lfHFitkI.TXHlwFe3bXXo2', 'EMPLEADO', true),
('María',    'López',      'maria@logitrack.com',    '$2a$10$5YZqxHRVXc1ANKbXA6t4Ne8YR/4V6G4lfHFitkI.TXHlwFe3bXXo2', 'EMPLEADO', true),
('Andrés',   'Martínez',   'andres@logitrack.com',   '$2a$10$5YZqxHRVXc1ANKbXA6t4Ne8YR/4V6G4lfHFitkI.TXHlwFe3bXXo2', 'EMPLEADO', true);

-- ---- BODEGAS ----
INSERT IGNORE INTO bodegas (nombre, ubicacion, capacidad, encargado, activo) VALUES
('Bodega Central',    'Bogotá, Cundinamarca',   5000, 'Carlos González',  true),
('Bodega Norte',      'Medellín, Antioquia',    3000, 'María López',       true),
('Bodega Sur',        'Cali, Valle del Cauca',  4000, 'Andrés Martínez',   true),
('Bodega Caribe',     'Barranquilla, Atlántico',2000, 'Pedro Ramírez',     true),
('Bodega Oriente',    'Bucaramanga, Santander', 3500, 'Ana Torres',        true);

-- ---- PRODUCTOS ----
INSERT IGNORE INTO productos (nombre, categoria, stock, precio, descripcion, activo) VALUES
('Laptop Dell XPS 15',     'Electrónica',  50,  4500000.00, 'Laptop empresarial 15 pulgadas',    true),
('Monitor LG 27"',         'Electrónica',  30,  900000.00,  'Monitor Full HD 27 pulgadas',       true),
('Teclado Mecánico',       'Periféricos',  80,  250000.00,  'Teclado mecánico retroiluminado',   true),
('Mouse Inalámbrico',      'Periféricos',  120, 85000.00,   'Mouse ergonómico inalámbrico',      true),
('Silla Ergonómica',       'Mobiliario',   25,  1200000.00, 'Silla de oficina con soporte lumbar',true),
('Escritorio Ejecutivo',   'Mobiliario',   15,  850000.00,  'Escritorio en L 180cm',             true),
('Papel A4 500 hojas',     'Papelería',   200,  18000.00,   'Resma de papel bond',               true),
('Bolígrafos x12',         'Papelería',   300,  12000.00,   'Caja de bolígrafos azules',         true),
('Impresora HP LaserJet',  'Electrónica',   8,  1800000.00, 'Impresora láser monocromática',     true),
('Auriculares Bluetooth',  'Periféricos',  45,  320000.00,  'Auriculares cancelación de ruido',  true);

-- ---- INVENTARIO INICIAL POR BODEGA ----
INSERT IGNORE INTO inventario_bodega (bodega_id, producto_id, stock_actual) VALUES
(1, 1, 20), (1, 2, 10), (1, 3, 30), (1, 4, 50), (1, 5, 10),
(1, 6,  5), (1, 7, 80), (1, 8,100), (1, 9,  3), (1,10, 20),
(2, 1, 15), (2, 2,  8), (2, 3, 20), (2, 4, 35), (2, 5,  8),
(2, 7, 60), (2, 8, 80), (2,10, 15),
(3, 1, 10), (3, 2,  7), (3, 4, 25), (3, 6,  5), (3, 7, 40),
(3, 8, 80), (3, 9,  3), (3,10, 10),
(4, 3, 15), (4, 4, 15), (4, 7, 30), (4, 8, 60),
(5, 1,  5), (5, 2,  5), (5, 3, 15), (5, 7, 30);
