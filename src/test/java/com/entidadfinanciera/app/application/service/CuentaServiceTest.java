package com.entidadfinanciera.app.application.service;

import com.entidadfinanciera.app.application.port.out.ClienteRepositoryPort;
import com.entidadfinanciera.app.application.port.out.CuentaRepositoryPort;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CuentaServiceTest {

    @Mock
    private CuentaRepositoryPort cuentaRepositoryPort;
    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @InjectMocks
    private CuentaService cuentaService;

    private Cliente clienteExistente() {
        return new Cliente(1L, TipoIdentificacion.CC, "123", "Juan", "Pérez",
                "juan@correo.com", null, null, null);
    }

    @Test
    void crear_cuentaAhorros_debeGenerarNumeroConPrefijo53YEstadoActiva() {
        when(clienteRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(clienteExistente()));
        when(cuentaRepositoryPort.existePorNumeroCuenta(anyString())).thenReturn(false);
        when(cuentaRepositoryPort.guardar(any(Cuenta.class))).thenAnswer(inv -> inv.getArgument(0));

        Cuenta resultado = cuentaService.crear(1L, TipoCuenta.AHORROS, false);

        assertThat(resultado.getNumeroCuenta()).startsWith("53");
        assertThat(resultado.getNumeroCuenta()).hasSize(10);
        assertThat(resultado.getEstado()).isEqualTo(EstadoCuenta.ACTIVA);
        assertThat(resultado.getSaldo()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void crear_cuentaCorriente_debeGenerarNumeroConPrefijo33() {
        when(clienteRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(clienteExistente()));
        when(cuentaRepositoryPort.existePorNumeroCuenta(anyString())).thenReturn(false);
        when(cuentaRepositoryPort.guardar(any(Cuenta.class))).thenAnswer(inv -> inv.getArgument(0));

        Cuenta resultado = cuentaService.crear(1L, TipoCuenta.CORRIENTE, false);

        assertThat(resultado.getNumeroCuenta()).startsWith("33");
    }

    @Test
    void crear_conClienteInexistente_debeLanzarExcepcion() {
        when(clienteRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cuentaService.crear(99L, TipoCuenta.AHORROS, false))
                .isInstanceOf(ClienteNoEncontradoException.class);
    }

    @Test
    void cambiarEstado_aCanceladaConSaldoCero_debePermitirlo() {
        Cuenta cuenta = new Cuenta(1L, TipoCuenta.AHORROS, "5300000001", EstadoCuenta.ACTIVA,
                BigDecimal.ZERO, false, LocalDateTime.now(), null, 1L, 0L);
        when(cuentaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(cuenta));
        when(cuentaRepositoryPort.guardar(any(Cuenta.class))).thenAnswer(inv -> inv.getArgument(0));

        Cuenta resultado = cuentaService.cambiarEstado(1L, EstadoCuenta.CANCELADA);

        assertThat(resultado.getEstado()).isEqualTo(EstadoCuenta.CANCELADA);
    }

    @Test
    void cambiarEstado_aCanceladaConSaldoPositivo_debeLanzarExcepcion() {
        Cuenta cuenta = new Cuenta(1L, TipoCuenta.AHORROS, "5300000001", EstadoCuenta.ACTIVA,
                new BigDecimal("50000"), false, LocalDateTime.now(), null, 1L, 0L);
        when(cuentaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(cuenta));

        assertThatThrownBy(() -> cuentaService.cambiarEstado(1L, EstadoCuenta.CANCELADA))
                .isInstanceOf(SaldoInvalidoParaCancelarException.class);
    }

    @Test
    void cambiarEstado_desdeCanceladaAActiva_debeLanzarExcepcion() {
        Cuenta cuenta = new Cuenta(1L, TipoCuenta.AHORROS, "5300000001", EstadoCuenta.CANCELADA,
                BigDecimal.ZERO, false, LocalDateTime.now(), null, 1L, 0L);
        when(cuentaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(cuenta));

        assertThatThrownBy(() -> cuentaService.cambiarEstado(1L, EstadoCuenta.ACTIVA))
                .isInstanceOf(TransicionEstadoInvalidaException.class);
    }
}