package com.entidadfinanciera.app.application.port.in;

import com.entidadfinanciera.app.domain.model.Cuenta;
import com.entidadfinanciera.app.domain.model.TipoCuenta;

public interface CrearCuentaUseCase {
    Cuenta crear(Long clienteId, TipoCuenta tipoCuenta, boolean exentaGmf);
}