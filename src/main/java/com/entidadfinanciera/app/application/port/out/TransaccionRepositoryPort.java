package com.entidadfinanciera.app.application.port.out;

import com.entidadfinanciera.app.domain.model.Transaccion;

import java.util.List;
import java.util.Optional;

public interface TransaccionRepositoryPort {
    Transaccion guardar(Transaccion transaccion);
    Optional<Transaccion> buscarPorId(Long id);
    List<Transaccion> listarPorCuenta(Long cuentaId);
}