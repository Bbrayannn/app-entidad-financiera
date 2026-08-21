package com.entidadfinanciera.app.application.port.in;

import com.entidadfinanciera.app.domain.model.Transaccion;

import java.math.BigDecimal;

public interface RealizarRetiroUseCase {
    Transaccion retirar(Long cuentaId, BigDecimal monto, String descripcion);
}