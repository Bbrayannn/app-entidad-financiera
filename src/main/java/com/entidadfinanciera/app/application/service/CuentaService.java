package com.entidadfinanciera.app.application.service;

import com.entidadfinanciera.app.application.port.in.CambiarEstadoCuentaUseCase;
import com.entidadfinanciera.app.application.port.in.ConsultarCuentaUseCase;
import com.entidadfinanciera.app.application.port.in.CrearCuentaUseCase;
import com.entidadfinanciera.app.application.port.out.ClienteRepositoryPort;
import com.entidadfinanciera.app.application.port.out.CuentaRepositoryPort;
import com.entidadfinanciera.app.domain.exception.*;
import com.entidadfinanciera.app.domain.model.Cuenta;
import com.entidadfinanciera.app.domain.model.EstadoCuenta;
import com.entidadfinanciera.app.domain.model.TipoCuenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * Orquesta creación de cuentas (con generación de número único) y cambios de estado.
 * No expone actualizar/eliminar genéricos porque el PDF solo permite cambiar el estado
 * de una cuenta, nunca editarla libremente ni borrarla.
 */

@Service
public class CuentaService implements CrearCuentaUseCase, CambiarEstadoCuentaUseCase, ConsultarCuentaUseCase {

    private static final int LONGITUD_NUMERO_CUENTA = 10;
    private static final int MAX_INTENTOS_GENERACION = 10;

    private final CuentaRepositoryPort cuentaRepositoryPort;
    private final ClienteRepositoryPort clienteRepositoryPort;
    private final SecureRandom random = new SecureRandom();

    public CuentaService(CuentaRepositoryPort cuentaRepositoryPort, ClienteRepositoryPort clienteRepositoryPort) {
        this.cuentaRepositoryPort = cuentaRepositoryPort;
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    /** Toda cuenta se crea activa con saldo $0, vinculada a un cliente que debe existir. */

    @Override
    @Transactional
    public Cuenta crear(Long clienteId, TipoCuenta tipoCuenta, boolean exentaGmf) {
        clienteRepositoryPort.buscarPorId(clienteId)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado con id " + clienteId));

        String numeroCuenta = generarNumeroCuentaUnico(tipoCuenta);

        Cuenta cuenta = new Cuenta(
                null,
                tipoCuenta,
                numeroCuenta,
                EstadoCuenta.ACTIVA,
                BigDecimal.ZERO,
                exentaGmf,
                LocalDateTime.now(),
                null,
                clienteId,
                null // version: Hibernate la asigna al insertar por primera vez
        );

        return cuentaRepositoryPort.guardar(cuenta);
    }


    /**
     * Genera un número de 10 dígitos con el numero dependiendo del tipo de cuenta (33/53) y reintenta
     * si choca con uno existente. Con 8 dígitos aleatorios la colisión es muy poco probable por que son aleatorios,
     * pero no imposible, así que el reintento cubre ese caso en vez de ignorarlo.
     */
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


    /**
     * Valida que la transición sea permitida (ver Cuenta.puedeTransicionarA) y, si el
     * destino es cancelada, que el saldo esté en $0 antes de aplicar el cambio.
     */

    @Override
    @Transactional
    public Cuenta cambiarEstado(Long id, EstadoCuenta nuevoEstado) {
        Cuenta cuenta = cuentaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada con id " + id));

        if (!cuenta.puedeTransicionarA(nuevoEstado)) {
            throw new TransicionEstadoInvalidaException(
                    "No se puede cambiar de " + cuenta.getEstado() + " a " + nuevoEstado + ".");
        }

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
    public Page<Cuenta> listarPorCliente(Long clienteId, Pageable pageable) {
        return cuentaRepositoryPort.listarPorCliente(clienteId, pageable);
    }
}