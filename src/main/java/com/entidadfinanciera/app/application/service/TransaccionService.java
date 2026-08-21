package com.entidadfinanciera.app.application.service;

import com.entidadfinanciera.app.application.port.in.*;
import com.entidadfinanciera.app.application.port.out.CuentaRepositoryPort;
import com.entidadfinanciera.app.application.port.out.TransaccionRepositoryPort;
import com.entidadfinanciera.app.domain.exception.*;
import com.entidadfinanciera.app.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransaccionService implements RealizarConsignacionUseCase, RealizarRetiroUseCase,
        RealizarTransferenciaUseCase, ConsultarTransaccionUseCase {

    private final TransaccionRepositoryPort transaccionRepositoryPort;
    private final CuentaRepositoryPort cuentaRepositoryPort;

    public TransaccionService(TransaccionRepositoryPort transaccionRepositoryPort,
                              CuentaRepositoryPort cuentaRepositoryPort) {
        this.transaccionRepositoryPort = transaccionRepositoryPort;
        this.cuentaRepositoryPort = cuentaRepositoryPort;
    }

    @Override
    @Transactional
    public Transaccion consignar(Long cuentaId, BigDecimal monto, String descripcion) {
        validarMonto(monto);
        Cuenta cuenta = obtenerCuentaActiva(cuentaId);

        BigDecimal nuevoSaldo = cuenta.getSaldo().add(monto);

        Transaccion transaccion = new Transaccion(null, TipoTransaccion.CONSIGNACION, monto,
                descripcion, LocalDateTime.now(), List.of(
                new Movimiento(null, null, cuentaId, TipoMovimiento.CREDITO, monto, nuevoSaldo, LocalDateTime.now())
        ));

        Transaccion guardada = transaccionRepositoryPort.guardar(transaccion);
        cuentaRepositoryPort.actualizarSaldo(cuentaId, nuevoSaldo, LocalDateTime.now());

        return guardada;
    }

    @Override
    @Transactional
    public Transaccion retirar(Long cuentaId, BigDecimal monto, String descripcion) {
        validarMonto(monto);
        Cuenta cuenta = obtenerCuentaActiva(cuentaId);

        if (cuenta.getSaldo().compareTo(monto) < 0) {
            throw new SaldoInsuficienteException(
                    "Saldo insuficiente. Saldo actual: " + cuenta.getSaldo() + ", monto solicitado: " + monto);
        }

        BigDecimal nuevoSaldo = cuenta.getSaldo().subtract(monto);
        cuenta.validarSaldoParaTipo(nuevoSaldo);

        Transaccion transaccion = new Transaccion(null, TipoTransaccion.RETIRO, monto,
                descripcion, LocalDateTime.now(), List.of(
                new Movimiento(null, null, cuentaId, TipoMovimiento.DEBITO, monto, nuevoSaldo, LocalDateTime.now())
        ));

        Transaccion guardada = transaccionRepositoryPort.guardar(transaccion);
        cuentaRepositoryPort.actualizarSaldo(cuentaId, nuevoSaldo, LocalDateTime.now());

        return guardada;
    }

    @Override
    @Transactional
    public Transaccion transferir(Long cuentaOrigenId, Long cuentaDestinoId, BigDecimal monto, String descripcion) {
        validarMonto(monto);

        if (cuentaOrigenId.equals(cuentaDestinoId)) {
            throw new TransferenciaEntreMismaCuentaException(
                    "No se puede transferir entre la misma cuenta.");
        }

        Cuenta origen = obtenerCuentaActiva(cuentaOrigenId);
        Cuenta destino = obtenerCuentaActiva(cuentaDestinoId);

        if (origen.getSaldo().compareTo(monto) < 0) {
            throw new SaldoInsuficienteException(
                    "Saldo insuficiente en cuenta origen. Saldo actual: " + origen.getSaldo());
        }

        BigDecimal nuevoSaldoOrigen = origen.getSaldo().subtract(monto);
        origen.validarSaldoParaTipo(nuevoSaldoOrigen);
        BigDecimal nuevoSaldoDestino = destino.getSaldo().add(monto);

        Transaccion transaccion = new Transaccion(null, TipoTransaccion.TRANSFERENCIA, monto,
                descripcion, LocalDateTime.now(), List.of(
                new Movimiento(null, null, cuentaOrigenId, TipoMovimiento.DEBITO, monto, nuevoSaldoOrigen, LocalDateTime.now()),
                new Movimiento(null, null, cuentaDestinoId, TipoMovimiento.CREDITO, monto, nuevoSaldoDestino, LocalDateTime.now())
        ));

        Transaccion guardada = transaccionRepositoryPort.guardar(transaccion);

        // Ambas actualizaciones de saldo, dentro de la MISMA transacción @Transactional.
        // Si la segunda falla, Spring revierte automáticamente la primera (y el insert de arriba).
        cuentaRepositoryPort.actualizarSaldo(cuentaOrigenId, nuevoSaldoOrigen, LocalDateTime.now());
        cuentaRepositoryPort.actualizarSaldo(cuentaDestinoId, nuevoSaldoDestino, LocalDateTime.now());

        return guardada;
    }

    private Cuenta obtenerCuentaActiva(Long cuentaId) {
        Cuenta cuenta = cuentaRepositoryPort.buscarPorId(cuentaId)
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada con id " + cuentaId));

        if (cuenta.getEstado() != EstadoCuenta.ACTIVA) {
            throw new CuentaInactivaException(
                    "La cuenta " + cuenta.getNumeroCuenta() + " no está activa. Estado actual: " + cuenta.getEstado());
        }
        return cuenta;
    }

    private void validarMonto(BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new MontoInvalidoException("El monto de la transacción debe ser mayor a cero.");
        }
    }

    @Override
    public Transaccion buscarPorId(Long id) {
        return transaccionRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada con id " + id));
    }

    @Override
    public List<Transaccion> listarPorCuenta(Long cuentaId) {
        return transaccionRepositoryPort.listarPorCuenta(cuentaId);
    }
}