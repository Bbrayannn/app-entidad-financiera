package com.entidadfinanciera.app.application.port.in;

import com.entidadfinanciera.app.domain.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConsultarClienteUseCase {
    Cliente buscarPorId(Long id);
    Page<Cliente> listarTodos(Pageable pageable);
}