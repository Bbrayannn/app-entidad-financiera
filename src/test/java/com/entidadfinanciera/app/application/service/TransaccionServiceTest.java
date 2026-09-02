package com.entidadfinanciera.app.application.service;

import com.entidadfinanciera.app.application.port.out.CuentaRepositoryPort;
import com.entidadfinanciera.app.application.port.out.TransaccionRepositoryPort;
import com.entidadfinanciera.app.domain.exception.*;
import com.entidadfinanciera.app.domain.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransaccionServiceTest {

    @Mock
    private TransaccionRepositoryPort transaccionRepositoryPort;
    @Mock
    private CuentaRepositoryPort cuentaRepositoryPort;

    @InjectMocks
    private TransaccionService transaccionService;

    private Cuenta cuentaActiva(Long id, BigDecimal saldo) {
        return new Cuenta(id, TipoCuenta.AHORROS, "530000000" + id, EstadoCuenta.ACTIVA,
                saldo, false, LocalDateTime.now(), null, 1L, 0L);
    }

    @Test
    void consignar_conDatosValidos_debeAumentarSaldo() {
        Cuenta cuenta = cuentaActiva(1L, new BigDecimal("100000"));
        when(cuentaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(cuenta));
        when(transaccionRepositoryPort.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        transaccionService.consignar(1L, new BigDecimal("50000"), "Depósito");

        verify(cuentaRepositoryPort).actualizarSaldo(eq(1L), eq(new BigDecimal("150000")), any());
    }

    @Test
    void consignar_conMontoCero_debeLanzarExcepcion() {
        assertThatThrownBy(() -> transaccionService.consignar(1L, BigDecimal.ZERO, "Inválido"))
                .isInstanceOf(MontoInvalidoException.class);

        verifyNoInteractions(cuentaRepositoryPort);
    }

    @Test
    void retirar_conSaldoInsuficiente_debeLanzarExcepcion() {
        Cuenta cuenta = cuentaActiva(1L, new BigDecimal("10000"));
        when(cuentaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(cuenta));

        assertThatThrownBy(() -> transaccionService.retirar(1L, new BigDecimal("50000"), "Retiro"))
                .isInstanceOf(SaldoInsuficienteException.class);

        verify(cuentaRepositoryPort, never()).actualizarSaldo(any(), any(), any());
    }

    @Test
    void retirar_montoIgualAlSaldo_debeDejarSaldoEnCero() {
        Cuenta cuenta = cuentaActiva(1L, new BigDecimal("50000"));
        when(cuentaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(cuenta));
        when(transaccionRepositoryPort.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        transaccionService.retirar(1L, new BigDecimal("50000"), "Retiro total");

        verify(cuentaRepositoryPort).actualizarSaldo(eq(1L), eq(BigDecimal.ZERO), any());
    }

    @Test
    void consignar_enCuentaInactiva_debeLanzarExcepcion() {
        Cuenta cuenta = new Cuenta(1L, TipoCuenta.AHORROS, "5300000001", EstadoCuenta.INACTIVA,
                BigDecimal.ZERO, false, LocalDateTime.now(), null, 1L, 0L);
        when(cuentaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(cuenta));

        assertThatThrownBy(() -> transaccionService.consignar(1L, new BigDecimal("10000"), "Depósito"))
                .isInstanceOf(CuentaInactivaException.class);
    }

    @Test
    void transferir_conDatosValidos_debeActualizarAmbasCuentas() {
        Cuenta origen = cuentaActiva(1L, new BigDecimal("100000"));
        Cuenta destino = cuentaActiva(2L, new BigDecimal("20000"));
        when(cuentaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(origen));
        when(cuentaRepositoryPort.buscarPorId(2L)).thenReturn(Optional.of(destino));
        when(transaccionRepositoryPort.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        var resultado = transaccionService.transferir(1L, 2L, new BigDecimal("30000"), "Pago");

        assertThat(resultado.getMovimientos()).hasSize(2);
        verify(cuentaRepositoryPort).actualizarSaldo(eq(1L), eq(new BigDecimal("70000")), any());
        verify(cuentaRepositoryPort).actualizarSaldo(eq(2L), eq(new BigDecimal("50000")), any());
    }

    @Test
    void transferir_entreLaMismaCuenta_debeLanzarExcepcion() {
        assertThatThrownBy(() -> transaccionService.transferir(1L, 1L, new BigDecimal("1000"), "Inválida"))
                .isInstanceOf(TransferenciaEntreMismaCuentaException.class);

        verifyNoInteractions(cuentaRepositoryPort);
    }

    @Test
    void transferir_conCuentaDestinoInexistente_debeLanzarExcepcionYNoAfectarOrigen() {
        Cuenta origen = cuentaActiva(1L, new BigDecimal("100000"));
        when(cuentaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(origen));
        when(cuentaRepositoryPort.buscarPorId(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transaccionService.transferir(1L, 2L, new BigDecimal("30000"), "Pago"))
                .isInstanceOf(CuentaNoEncontradaException.class);

        verify(cuentaRepositoryPort, never()).actualizarSaldo(any(), any(), any());
    }
}