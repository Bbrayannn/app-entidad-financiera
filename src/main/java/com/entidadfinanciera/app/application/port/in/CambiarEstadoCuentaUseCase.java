package com.entidadfinanciera.app.application.port.in;

import com.entidadfinanciera.app.domain.model.Cuenta;
import com.entidadfinanciera.app.domain.model.EstadoCuenta;

public interface CambiarEstadoCuentaUseCase {
    Cuenta cambiarEstado(Long id, EstadoCuenta nuevoEstado);
}