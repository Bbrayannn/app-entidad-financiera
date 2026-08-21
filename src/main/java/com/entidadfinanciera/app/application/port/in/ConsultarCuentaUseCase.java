package com.entidadfinanciera.app.application.port.in;

import com.entidadfinanciera.app.domain.model.Cuenta;

import java.util.List;

public interface ConsultarCuentaUseCase {
    Cuenta buscarPorId(Long id);
    List<Cuenta> listarPorCliente(Long clienteId);
}