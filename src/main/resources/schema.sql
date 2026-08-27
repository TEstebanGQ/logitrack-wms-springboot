
CREATE DATABASE IF NOT EXISTS logitrack_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE logitrack_db;

-- ---- 1. USUARIOS ----
CREATE TABLE IF NOT EXISTS usuarios (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(100)                    NOT NULL,
    apellido   VARCHAR(100)                    NOT NULL,
    email      VARCHAR(150)                    NOT NULL UNIQUE,
    password   VARCHAR(255)                    NOT NULL,
    rol        ENUM('ADMIN', 'EMPLEADO')       NOT NULL DEFAULT 'EMPLEADO',
    activo     BOOLEAN                         NOT NULL DEFAULT TRUE,
    created_at DATETIME                        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME                        ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ---- 2. BODEGAS ----
CREATE TABLE IF NOT EXISTS bodegas (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(150) NOT NULL,
    ubicacion  VARCHAR(255) NOT NULL,
    capacidad  INT          NOT NULL,
    encargado  VARCHAR(150) NOT NULL,
    activo     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ---- 3. PRODUCTOS ----
CREATE TABLE IF NOT EXISTS productos (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(150)   NOT NULL,
    categoria   VARCHAR(100)   NOT NULL,
    stock       INT            NOT NULL DEFAULT 0,
    precio      DECIMAL(10, 2) NOT NULL,
    descripcion VARCHAR(500)   NULL,
    activo      BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME       ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ---- 4. MOVIMIENTOS ----
CREATE TABLE IF NOT EXISTS movimientos (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_movimiento    ENUM('ENTRADA', 'SALIDA', 'TRANSFERENCIA') NOT NULL,
    fecha              DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observaciones      VARCHAR(500) NULL,
    usuario_id         BIGINT      NOT NULL,
    bodega_origen_id   BIGINT      NULL,
    bodega_destino_id  BIGINT      NULL,
    CONSTRAINT fk_mov_usuario  FOREIGN KEY (usuario_id)        REFERENCES usuarios(id),
    CONSTRAINT fk_mov_origen   FOREIGN KEY (bodega_origen_id)  REFERENCES bodegas(id),
    CONSTRAINT fk_mov_destino  FOREIGN KEY (bodega_destino_id) REFERENCES bodegas(id)
) ENGINE=InnoDB;

-- ---- 5. MOVIMIENTO_DETALLE ----
CREATE TABLE IF NOT EXISTS movimiento_detalle (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    movimiento_id   BIGINT         NOT NULL,
    producto_id     BIGINT         NOT NULL,
    cantidad        INT            NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_det_movimiento FOREIGN KEY (movimiento_id) REFERENCES movimientos(id) ON DELETE CASCADE,
    CONSTRAINT fk_det_producto   FOREIGN KEY (producto_id)   REFERENCES productos(id),
    UNIQUE KEY uq_mov_prod (movimiento_id, producto_id)
) ENGINE=InnoDB;

-- ---- 6. INVENTARIO_BODEGA ----
CREATE TABLE IF NOT EXISTS inventario_bodega (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    bodega_id            BIGINT   NOT NULL,
    producto_id          BIGINT   NOT NULL,
    stock_actual         INT      NOT NULL DEFAULT 0,
    ultima_actualizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_inv_bodega   FOREIGN KEY (bodega_id)   REFERENCES bodegas(id),
    CONSTRAINT fk_inv_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    UNIQUE KEY uq_bodega_producto (bodega_id, producto_id)
) ENGINE=InnoDB;

-- ---- 7. AUDITORIA ----
CREATE TABLE IF NOT EXISTS auditoria (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    entidad             VARCHAR(100)                        NOT NULL,
    entidad_id          BIGINT                              NULL,
    tipo_operacion      ENUM('INSERT', 'UPDATE', 'DELETE')  NOT NULL,
    fecha_hora          DATETIME                            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_id          BIGINT                              NULL,
    valores_anteriores  TEXT                                NULL,
    valores_nuevos      TEXT                                NULL,
    ip_address          VARCHAR(50)                         NULL,
    descripcion         VARCHAR(500)                        NULL,
    CONSTRAINT fk_aud_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ---- ÍNDICES ----
-- Nota: Los índices de FK se crean automáticamente por MySQL.
-- Los índices adicionales se crean via data.sql o manualmente:
-- CREATE INDEX idx_movimientos_fecha ON movimientos(fecha);
-- CREATE INDEX idx_auditoria_usuario ON auditoria(usuario_id);
-- CREATE INDEX idx_productos_stock   ON productos(stock);

