-- ---- 1. USUARIOS ----
CREATE TABLE IF NOT EXISTS usuarios (
    id         BIGSERIAL PRIMARY KEY,
    nombre     VARCHAR(100)                    NOT NULL,
    apellido   VARCHAR(100)                    NOT NULL,
    email      VARCHAR(150)                    NOT NULL UNIQUE,
    password   VARCHAR(255)                    NOT NULL,
    rol        VARCHAR(20)                     NOT NULL DEFAULT 'EMPLEADO',
    activo     BOOLEAN                         NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP                       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP                       NULL
);
ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS usuarios_rol_check;


-- ---- 2. BODEGAS ----
CREATE TABLE IF NOT EXISTS bodegas (
    id         BIGSERIAL PRIMARY KEY,
    nombre     VARCHAR(150) NOT NULL,
    ubicacion  VARCHAR(255) NOT NULL,
    capacidad  INT          NOT NULL,
    encargado  VARCHAR(150) NOT NULL,
    activo     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP     NULL
);

-- ---- 3. PRODUCTOS ----
CREATE TABLE IF NOT EXISTS productos (
    id           BIGSERIAL PRIMARY KEY,
    nombre       VARCHAR(150)   NOT NULL,
    categoria_id BIGINT         NULL,
    stock        INT            NOT NULL DEFAULT 0,
    precio       DECIMAL(10, 2) NOT NULL,
    descripcion  VARCHAR(500)   NULL,
    activo       BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP       NULL
);

-- ---- 4. MOVIMIENTOS ----
CREATE TABLE IF NOT EXISTS movimientos (
    id                 BIGSERIAL PRIMARY KEY,
    tipo_movimiento    VARCHAR(20) NOT NULL,
    fecha              TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observaciones      VARCHAR(500) NULL,
    usuario_id         BIGINT      NOT NULL,
    bodega_origen_id   BIGINT      NULL,
    bodega_destino_id  BIGINT      NULL,
    CONSTRAINT fk_mov_usuario  FOREIGN KEY (usuario_id)        REFERENCES usuarios(id),
    CONSTRAINT fk_mov_origen   FOREIGN KEY (bodega_origen_id)  REFERENCES bodegas(id),
    CONSTRAINT fk_mov_destino  FOREIGN KEY (bodega_destino_id) REFERENCES bodegas(id)
);

-- ---- 5. MOVIMIENTO_DETALLE ----
CREATE TABLE IF NOT EXISTS movimiento_detalle (
    id              BIGSERIAL PRIMARY KEY,
    movimiento_id   BIGINT         NOT NULL,
    producto_id     BIGINT         NOT NULL,
    cantidad        INT            NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_det_movimiento FOREIGN KEY (movimiento_id) REFERENCES movimientos(id) ON DELETE CASCADE,
    CONSTRAINT fk_det_producto   FOREIGN KEY (producto_id)   REFERENCES productos(id),
    CONSTRAINT uq_mov_prod UNIQUE (movimiento_id, producto_id)
);

-- ---- 6. INVENTARIO_BODEGA ----
CREATE TABLE IF NOT EXISTS inventario_bodega (
    id                   BIGSERIAL PRIMARY KEY,
    bodega_id            BIGINT   NOT NULL,
    producto_id          BIGINT   NOT NULL,
    stock_actual         INT      NOT NULL DEFAULT 0,
    ultima_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_inv_bodega   FOREIGN KEY (bodega_id)   REFERENCES bodegas(id),
    CONSTRAINT fk_inv_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    CONSTRAINT uq_bodega_producto UNIQUE (bodega_id, producto_id)
);

-- ---- 7. AUDITORIA ----
CREATE TABLE IF NOT EXISTS auditoria (
    id                  BIGSERIAL PRIMARY KEY,
    entidad             VARCHAR(100)                        NOT NULL,
    entidad_id          BIGINT                              NULL,
    tipo_operacion      VARCHAR(20)                         NOT NULL,
    fecha_hora          TIMESTAMP                           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_id          BIGINT                              NULL,
    valores_anteriores  TEXT                                NULL,
    valores_nuevos      TEXT                                NULL,
    ip_address          VARCHAR(50)                         NULL,
    descripcion         VARCHAR(500)                        NULL,
    CONSTRAINT fk_aud_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
);


CREATE TABLE IF NOT EXISTS categorias (
    id          BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(500),
    activo      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP
);

CREATE TABLE IF NOT EXISTS proveedores (
    id          BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(150) NOT NULL,
    ruc         VARCHAR(50),
    telefono    VARCHAR(20),
    email       VARCHAR(150),
    direccion   VARCHAR(300),
    activo      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP
);

CREATE TABLE IF NOT EXISTS clientes (
    id          BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(150) NOT NULL,
    ruc         VARCHAR(50),
    telefono    VARCHAR(20),
    email       VARCHAR(150),
    direccion   VARCHAR(300),
    activo      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP
);



CREATE TABLE IF NOT EXISTS alertas_stock (
    id              BIGSERIAL PRIMARY KEY,
    producto_id     BIGINT NOT NULL REFERENCES productos(id),
    bodega_id       BIGINT NOT NULL REFERENCES bodegas(id),
    stock_actual    INT NOT NULL,
    stock_minimo    INT NOT NULL,
    estado          VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    fecha_generada  TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_resuelta  TIMESTAMP,
    resuelta_por_id BIGINT REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS notificaciones (
    id              BIGSERIAL PRIMARY KEY,
    usuario_id      BIGINT NOT NULL REFERENCES usuarios(id),
    titulo          VARCHAR(200) NOT NULL,
    mensaje         VARCHAR(1000) NOT NULL,
    tipo            VARCHAR(20) NOT NULL DEFAULT 'INFO',
    leida           BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion  TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_lectura   TIMESTAMP,
    url_accion      VARCHAR(500)
);



ALTER TABLE productos DROP COLUMN IF EXISTS categoria;
ALTER TABLE productos ADD COLUMN IF NOT EXISTS categoria_id BIGINT REFERENCES categorias(id);
ALTER TABLE productos ADD COLUMN IF NOT EXISTS stock_minimo INT NOT NULL DEFAULT 10;
ALTER TABLE movimientos ADD COLUMN IF NOT EXISTS proveedor_id BIGINT REFERENCES proveedores(id);
ALTER TABLE movimientos ADD COLUMN IF NOT EXISTS cliente_id BIGINT REFERENCES clientes(id);


-- Reemplaza la generación con new Random() que tenía condición de carrera bajo concurrencia.
-- El valor de la secuencia es único a nivel de base de datos y no puede colisionar.
CREATE SEQUENCE IF NOT EXISTS orden_compra_seq START WITH 1 INCREMENT BY 1;

-- Índices optimizados en claves foráneas de alta frecuencia para acelerar el tiempo de consulta.
CREATE INDEX IF NOT EXISTS idx_movimientos_usuario_id   ON movimientos(usuario_id);
CREATE INDEX IF NOT EXISTS idx_movimientos_origen_id    ON movimientos(bodega_origen_id);
CREATE INDEX IF NOT EXISTS idx_movimientos_destino_id   ON movimientos(bodega_destino_id);
CREATE INDEX IF NOT EXISTS idx_movimiento_det_producto  ON movimiento_detalle(producto_id);
CREATE INDEX IF NOT EXISTS idx_auditoria_usuario_id     ON auditoria(usuario_id);
CREATE INDEX IF NOT EXISTS idx_auditoria_fecha_hora     ON auditoria(fecha_hora);
CREATE INDEX IF NOT EXISTS idx_notificaciones_usuario   ON notificaciones(usuario_id);
CREATE INDEX IF NOT EXISTS idx_notificaciones_leida     ON notificaciones(usuario_id, leida);
CREATE INDEX IF NOT EXISTS idx_alertas_producto         ON alertas_stock(producto_id);
CREATE INDEX IF NOT EXISTS idx_alertas_bodega           ON alertas_stock(bodega_id);
