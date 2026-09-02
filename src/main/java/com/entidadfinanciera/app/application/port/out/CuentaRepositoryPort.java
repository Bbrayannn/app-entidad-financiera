package com.entidadfinanciera.app.application.port.out;

import com.entidadfinanciera.app.domain.model.Cuenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface CuentaRepositoryPort {
    Cuenta guardar(Cuenta cuenta);
    Optional<Cuenta> buscarPorId(Long id);
    Page<Cuenta> listarPorCliente(Long clienteId, Pageable pageable);
    boolean existePorNumeroCuenta(String numeroCuenta);
    boolean existenCuentasParaCliente(Long clienteId);
    void actualizarSaldo(Long cuentaId, BigDecimal nuevoSaldo, LocalDateTime fechaModificacion);
}