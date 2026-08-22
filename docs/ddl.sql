CREATE TABLE clientes (
                          id BIGSERIAL PRIMARY KEY,
                          tipo_identificacion VARCHAR(20) NOT NULL,
                          numero_identificacion VARCHAR(20) NOT NULL UNIQUE,
                          nombres VARCHAR(100) NOT NULL CHECK (char_length(nombres) >= 2),
                          apellido VARCHAR(100) NOT NULL CHECK (char_length(apellido) >= 2),
                          correo_electronico VARCHAR(150) NOT NULL UNIQUE,
                          fecha_nacimiento DATE NOT NULL,
                          fecha_creacion TIMESTAMP NOT NULL DEFAULT now(),
                          fecha_modificacion TIMESTAMP
);

CREATE TABLE cuentas (
                         id BIGSERIAL PRIMARY KEY,
                         tipo_cuenta VARCHAR(20) NOT NULL CHECK (tipo_cuenta IN ('CORRIENTE', 'AHORROS')),
                         numero_cuenta CHAR(10) NOT NULL UNIQUE CHECK (numero_cuenta ~ '^[0-9]{10}$'),
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('ACTIVA', 'INACTIVA', 'CANCELADA')),
    saldo NUMERIC(15,2) NOT NULL DEFAULT 0,
    exenta_gmf BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT now(),
    fecha_modificacion TIMESTAMP,
    cliente_id BIGINT NOT NULL REFERENCES clientes(id)
);

CREATE INDEX idx_cuentas_cliente_id ON cuentas(cliente_id);

CREATE TABLE transacciones (
                               id BIGSERIAL PRIMARY KEY,
                               tipo_transaccion VARCHAR(20) NOT NULL CHECK (tipo_transaccion IN ('CONSIGNACION', 'RETIRO', 'TRANSFERENCIA')),
                               monto NUMERIC(15,2) NOT NULL CHECK (monto > 0),
                               descripcion VARCHAR(255),
                               fecha_creacion TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE movimientos (
                             id BIGSERIAL PRIMARY KEY,
                             transaccion_id BIGINT NOT NULL REFERENCES transacciones(id),
                             cuenta_id BIGINT NOT NULL REFERENCES cuentas(id),
                             tipo_movimiento VARCHAR(10) NOT NULL CHECK (tipo_movimiento IN ('DEBITO', 'CREDITO')),
                             monto NUMERIC(15,2) NOT NULL CHECK (monto > 0),
                             saldo_resultante NUMERIC(15,2) NOT NULL,
                             fecha_creacion TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_movimientos_cuenta_fecha ON movimientos(cuenta_id, fecha_creacion);