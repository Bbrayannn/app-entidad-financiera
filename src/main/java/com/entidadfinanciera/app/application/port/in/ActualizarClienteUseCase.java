package com.entidadfinanciera.app.application.port.in;

import com.entidadfinanciera.app.domain.model.Cliente;

public interface ActualizarClienteUseCase {
    Cliente actualizar(Long id, Cliente cliente);
}