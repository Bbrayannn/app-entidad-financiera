package com.entidadfinanciera.app.application.port.out;

import com.entidadfinanciera.app.domain.model.Cuenta;

import java.util.List;
import java.util.Optional;

public interface CuentaRepositoryPort {
    Cuenta guardar(Cuenta cuenta);
    Optional<Cuenta> buscarPorId(Long id);
    List<Cuenta> listarPorCliente(Long clienteId);
    boolean existePorNumeroCuenta(String numeroCuenta);
    boolean existenCuentasParaCliente(Long clienteId);
    void eliminar(Long id);
}