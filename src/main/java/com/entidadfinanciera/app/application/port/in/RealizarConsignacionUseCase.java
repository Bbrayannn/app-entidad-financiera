package com.entidadfinanciera.app.application.port.in;

import com.entidadfinanciera.app.domain.model.Transaccion;

import java.math.BigDecimal;

public interface RealizarConsignacionUseCase {
    Transaccion consignar(Long cuentaId, BigDecimal monto, String descripcion);
}