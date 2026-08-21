package com.entidadfinanciera.app.application.port.in;

import com.entidadfinanciera.app.domain.model.Transaccion;

import java.util.List;

public interface ConsultarTransaccionUseCase {
    Transaccion buscarPorId(Long id);
    List<Transaccion> listarPorCuenta(Long cuentaId);
}