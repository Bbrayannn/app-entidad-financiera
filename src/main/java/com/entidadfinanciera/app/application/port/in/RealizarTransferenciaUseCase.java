package com.entidadfinanciera.app.application.port.in;

import com.entidadfinanciera.app.domain.model.Transaccion;

import java.math.BigDecimal;

public interface RealizarTransferenciaUseCase {
    Transaccion transferir(Long cuentaOrigenId, Long cuentaDestinoId, BigDecimal monto, String descripcion);
}