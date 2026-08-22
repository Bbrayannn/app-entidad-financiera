-- ================================
-- DML: Ciclo de vida completo de datos
-- ================================

-- 1. Crear un cliente
INSERT INTO clientes (tipo_identificacion, numero_identificacion, nombres, apellido, correo_electronico, fecha_nacimiento, fecha_creacion)
VALUES ('CC', '1023456789', 'Juan', 'Pérez', 'juan.perez@correo.com', '1995-04-12', now());

-- 2. Crear una cuenta de ahorros para ese cliente (activa por defecto, saldo inicial 0)
INSERT INTO cuentas (tipo_cuenta, numero_cuenta, estado, saldo, exenta_gmf, fecha_creacion, cliente_id)
VALUES ('AHORROS', '5300000001', 'ACTIVA', 0, false, now(), 1);

-- 3. Crear una segunda cuenta (corriente) para el mismo cliente
INSERT INTO cuentas (tipo_cuenta, numero_cuenta, estado, saldo, exenta_gmf, fecha_creacion, cliente_id)
VALUES ('CORRIENTE', '3300000001', 'ACTIVA', 0, false, now(), 1);

-- 4. Registrar una consignación de $100.000 a la cuenta de ahorros
INSERT INTO transacciones (tipo_transaccion, monto, descripcion, fecha_creacion)
VALUES ('CONSIGNACION', 100000, 'Depósito inicial', now());

INSERT INTO movimientos (transaccion_id, cuenta_id, tipo_movimiento, monto, saldo_resultante, fecha_creacion)
VALUES (1, 1, 'CREDITO', 100000, 100000, now());

UPDATE cuentas SET saldo = 100000, fecha_modificacion = now() WHERE id = 1;

-- 5. Registrar una transferencia de $30.000 de ahorros hacia corriente
INSERT INTO transacciones (tipo_transaccion, monto, descripcion, fecha_creacion)
VALUES ('TRANSFERENCIA', 30000, 'Pago entre cuentas propias', now());

INSERT INTO movimientos (transaccion_id, cuenta_id, tipo_movimiento, monto, saldo_resultante, fecha_creacion)
VALUES (2, 1, 'DEBITO', 30000, 70000, now());

INSERT INTO movimientos (transaccion_id, cuenta_id, tipo_movimiento, monto, saldo_resultante, fecha_creacion)
VALUES (2, 2, 'CREDITO', 30000, 30000, now());

UPDATE cuentas SET saldo = 70000, fecha_modificacion = now() WHERE id = 1;
UPDATE cuentas SET saldo = 30000, fecha_modificacion = now() WHERE id = 2;

-- 6. Consultar el "estado de cuenta" (historial de movimientos) de la cuenta de ahorros
SELECT m.id, m.tipo_movimiento, m.monto, m.saldo_resultante, m.fecha_creacion, t.tipo_transaccion, t.descripcion
FROM movimientos m
         JOIN transacciones t ON t.id = m.transaccion_id
WHERE m.cuenta_id = 1
ORDER BY m.fecha_creacion DESC;

-- 7. Intentar cancelar la cuenta de ahorros (debe fallar en la aplicación: saldo != 0)
-- Nota: esta regla vive en el Service, no en un CHECK de BD, porque depende
-- de una transición de estado, no solo del valor del saldo en aislamiento.
-- UPDATE cuentas SET estado = 'CANCELADA' WHERE id = 1; -- rechazado por la aplicación, no se ejecuta aquí

-- 8. Ejemplo de eliminación de cliente rechazada por regla de negocio
-- DELETE FROM clientes WHERE id = 1; -- rechazado por la aplicación (tiene cuentas vinculadas)