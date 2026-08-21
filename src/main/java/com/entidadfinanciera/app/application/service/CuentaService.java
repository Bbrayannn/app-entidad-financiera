package com.entidadfinanciera.app.application.service;

import com.entidadfinanciera.app.application.port.in.CambiarEstadoCuentaUseCase;
import com.entidadfinanciera.app.application.port.in.ConsultarCuentaUseCase;
import com.entidadfinanciera.app.application.port.in.CrearCuentaUseCase;
import com.entidadfinanciera.app.application.port.in.EliminarCuentaUseCase;
import com.entidadfinanciera.app.application.port.out.ClienteRepositoryPort;
import com.entidadfinanciera.app.application.port.out.CuentaRepositoryPort;
import com.entidadfinanciera.app.domain.exception.*;
import com.entidadfinanciera.app.domain.model.Cuenta;
import com.entidadfinanciera.app.domain.model.EstadoCuenta;
import com.entidadfinanciera.app.domain.model.TipoCuenta;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CuentaService implements CrearCuentaUseCase, CambiarEstadoCuentaUseCase,
        ConsultarCuentaUseCase, EliminarCuentaUseCase {

    private static final int LONGITUD_NUMERO_CUENTA = 10;
    private static final int MAX_INTENTOS_GENERACION = 10;

    private final CuentaRepositoryPort cuentaRepositoryPort;
    private final ClienteRepositoryPort clienteRepositoryPort;
    private final SecureRandom random = new SecureRandom();

    public CuentaService(CuentaRepositoryPort cuentaRepositoryPort, ClienteRepositoryPort clienteRepositoryPort) {
        this.cuentaRepositoryPort = cuentaRepositoryPort;
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    @Override
    @Transactional
    public Cuenta crear(Long clienteId, TipoCuenta tipoCuenta, boolean exentaGmf) {
        clienteRepositoryPort.buscarPorId(clienteId)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado con id " + clienteId));

        String numeroCuenta = generarNumeroCuentaUnico(tipoCuenta);

        // Regla del PDF: cuenta de ahorros se crea ACTIVA por defecto.
        // Aplicamos el mismo criterio por consistencia a cuenta corriente,
        // ya que el PDF no indica lo contrario.
        Cuenta cuenta = new Cuenta(
                null,
                tipoCuenta,
                numeroCuenta,
                EstadoCuenta.ACTIVA,
                BigDecimal.ZERO,
                exentaGmf,
                LocalDateTime.now(),
                null,
                clienteId
        );

        return cuentaRepositoryPort.guardar(cuenta);
    }

    private String generarNumeroCuentaUnico(TipoCuenta tipoCuenta) {
        for (int intento = 0; intento < MAX_INTENTOS_GENERACION; intento++) {
            String candidato = generarCandidato(tipoCuenta);
            if (!cuentaRepositoryPort.existePorNumeroCuenta(candidato)) {
                return candidato;
            }
        }
        throw new GeneracionNumeroCuentaException(
                "No fue posible generar un número de cuenta único tras " + MAX_INTENTOS_GENERACION + " intentos.");
    }

    private String generarCandidato(TipoCuenta tipoCuenta) {
        String prefijo = tipoCuenta.getPrefijo();
        int digitosRestantes = LONGITUD_NUMERO_CUENTA - prefijo.length();
        StringBuilder sb = new StringBuilder(prefijo);
        for (int i = 0; i < digitosRestantes; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    @Override
    @Transactional
    public Cuenta cambiarEstado(Long id, EstadoCuenta nuevoEstado) {
        Cuenta cuenta = cuentaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada con id " + id));

        if (nuevoEstado == EstadoCuenta.CANCELADA && !cuenta.puedeCancelarse()) {
            throw new SaldoInvalidoParaCancelarException(
                    "Solo se pueden cancelar cuentas con saldo igual a $0. Saldo actual: " + cuenta.getSaldo());
        }

        cuenta.setEstado(nuevoEstado);
        cuenta.setFechaModificacion(LocalDateTime.now());
        return cuentaRepositoryPort.guardar(cuenta);
    }

    @Override
    public Cuenta buscarPorId(Long id) {
        return cuentaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada con id " + id));
    }

    @Override
    public List<Cuenta> listarPorCliente(Long clienteId) {
        return cuentaRepositoryPort.listarPorCliente(clienteId);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Cuenta cuenta = cuentaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada con id " + id));

        if (cuenta.getEstado() != EstadoCuenta.CANCELADA) {
            throw new CuentaNoCanceladaException(
                    "Solo se pueden eliminar cuentas en estado CANCELADA. Estado actual: " + cuenta.getEstado());
        }

        cuentaRepositoryPort.eliminar(id);
    }
}