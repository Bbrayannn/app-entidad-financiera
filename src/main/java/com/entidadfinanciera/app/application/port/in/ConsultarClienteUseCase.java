package com.entidadfinanciera.app.application.port.in;

import com.entidadfinanciera.app.domain.model.Cliente;

import java.util.List;

public interface ConsultarClienteUseCase {
    Cliente buscarPorId(Long id);
    List<Cliente> listarTodos();
}