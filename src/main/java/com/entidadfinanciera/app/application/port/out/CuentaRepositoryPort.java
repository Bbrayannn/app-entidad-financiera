package com.entidadfinanciera.app.application.port.out;

import com.entidadfinanciera.app.domain.model.Cuenta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida: contrato de persistencia para Cuenta, implementado por el adaptador JPA.
 */

public interface CuentaRepositoryPort {
    Cuenta guardar(Cuenta cuenta);
    Optional<Cuenta> buscarPorId(Long id);
    List<Cuenta> listarPorCliente(Long clienteId);
    boolean existePorNumeroCuenta(String numeroCuenta);
    boolean existenCuentasParaCliente(Long clienteId);
    void eliminar(Long id);
    void actualizarSaldo(Long cuentaId, java.math.BigDecimal nuevoSaldo, java.time.LocalDateTime fechaModificacion);}