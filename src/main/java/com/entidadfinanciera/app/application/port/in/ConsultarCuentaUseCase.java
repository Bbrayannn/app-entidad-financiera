package com.entidadfinanciera.app.application.port.in;

import com.entidadfinanciera.app.domain.model.Cuenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConsultarCuentaUseCase {
    Cuenta buscarPorId(Long id);
    Page<Cuenta> listarPorCliente(Long clienteId, Pageable pageable);
}