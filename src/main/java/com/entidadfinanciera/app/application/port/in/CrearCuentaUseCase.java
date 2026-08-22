package com.entidadfinanciera.app.application.port.in;

import com.entidadfinanciera.app.domain.model.Cuenta;
import com.entidadfinanciera.app.domain.model.TipoCuenta;

/**
 * Puerto de entrada: define el caso de uso para la creación de nuevas cuentas bancarias.
 */

public interface CrearCuentaUseCase {
    Cuenta crear(Long clienteId, TipoCuenta tipoCuenta, boolean exentaGmf);
}