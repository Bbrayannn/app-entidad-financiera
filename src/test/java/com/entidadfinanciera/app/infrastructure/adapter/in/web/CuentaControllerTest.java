package com.entidadfinanciera.app.infrastructure.adapter.in.web;

import com.entidadfinanciera.app.application.port.in.CambiarEstadoCuentaUseCase;
import com.entidadfinanciera.app.application.port.in.ConsultarCuentaUseCase;
import com.entidadfinanciera.app.application.port.in.CrearCuentaUseCase;
import com.entidadfinanciera.app.domain.exception.ClienteNoEncontradoException;
import com.entidadfinanciera.app.domain.exception.CuentaNoEncontradaException;
import com.entidadfinanciera.app.domain.exception.SaldoInvalidoParaCancelarException;
import com.entidadfinanciera.app.domain.exception.TransicionEstadoInvalidaException;
import com.entidadfinanciera.app.domain.model.Cuenta;
import com.entidadfinanciera.app.domain.model.EstadoCuenta;
import com.entidadfinanciera.app.domain.model.TipoCuenta;
import com.entidadfinanciera.app.infrastructure.adapter.in.web.exception.GlobalExceptionHandler;
import com.entidadfinanciera.app.infrastructure.adapter.in.web.mapper.CuentaWebMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CuentaController.class)
@Import({CuentaWebMapper.class, GlobalExceptionHandler.class})
class CuentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CrearCuentaUseCase crearCuentaUseCase;
    @MockBean
    private CambiarEstadoCuentaUseCase cambiarEstadoCuentaUseCase;
    @MockBean
    private ConsultarCuentaUseCase consultarCuentaUseCase;

    private Cuenta cuentaEjemplo(Long id, EstadoCuenta estado, BigDecimal saldo) {
        return new Cuenta(id, TipoCuenta.AHORROS, "5300000001", estado, saldo,
                false, LocalDateTime.now(), null, 1L, 0L);
    }

    @Test
    void crear_conDatosValidos_debeRetornar201() throws Exception {
        Cuenta creada = cuentaEjemplo(1L, EstadoCuenta.ACTIVA, BigDecimal.ZERO);
        when(crearCuentaUseCase.crear(1L, TipoCuenta.AHORROS, false)).thenReturn(creada);

        mockMvc.perform(post("/api/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clienteId\":1,\"tipoCuenta\":\"AHORROS\",\"exentaGmf\":false}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCuenta").value("5300000001"))
                .andExpect(jsonPath("$.estado").value("ACTIVA"));
    }

    @Test
    void crear_sinClienteId_debeRetornar400() throws Exception {
        mockMvc.perform(post("/api/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipoCuenta\":\"AHORROS\",\"exentaGmf\":false}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_conTipoCuentaInvalido_debeRetornar400() throws Exception {
        mockMvc.perform(post("/api/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clienteId\":1,\"tipoCuenta\":\"XYZ\",\"exentaGmf\":false}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_conClienteInexistente_debeRetornar404() throws Exception {
        when(crearCuentaUseCase.crear(99L, TipoCuenta.AHORROS, false))
                .thenThrow(new ClienteNoEncontradoException("Cliente no encontrado con id 99"));

        mockMvc.perform(post("/api/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clienteId\":99,\"tipoCuenta\":\"AHORROS\",\"exentaGmf\":false}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarPorId_conIdExistente_debeRetornar200() throws Exception {
        when(consultarCuentaUseCase.buscarPorId(1L))
                .thenReturn(cuentaEjemplo(1L, EstadoCuenta.ACTIVA, new BigDecimal("50000")));

        mockMvc.perform(get("/api/cuentas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(50000));
    }

    @Test
    void buscarPorId_conIdInexistente_debeRetornar404() throws Exception {
        when(consultarCuentaUseCase.buscarPorId(99L))
                .thenThrow(new CuentaNoEncontradaException("Cuenta no encontrada con id 99"));

        mockMvc.perform(get("/api/cuentas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarPorCliente_debeRetornarPaginaDeCuentas() throws Exception {
        Cuenta cuenta = cuentaEjemplo(1L, EstadoCuenta.ACTIVA, new BigDecimal("10000"));
        Page<Cuenta> pagina = new PageImpl<>(List.of(cuenta), PageRequest.of(0, 20), 1);

        when(consultarCuentaUseCase.listarPorCliente(eq(1L), any(Pageable.class))).thenReturn(pagina);

        mockMvc.perform(get("/api/cuentas").param("clienteId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].numeroCuenta").value("5300000001"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void cambiarEstado_aCanceladaConSaldoPositivo_debeRetornar409() throws Exception {
        when(cambiarEstadoCuentaUseCase.cambiarEstado(1L, EstadoCuenta.CANCELADA))
                .thenThrow(new SaldoInvalidoParaCancelarException(
                        "Solo se pueden cancelar cuentas con saldo igual a $0."));

        mockMvc.perform(patch("/api/cuentas/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nuevoEstado\":\"CANCELADA\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("Solo se pueden cancelar cuentas con saldo igual a $0."));
    }

    @Test
    void cambiarEstado_desdeCanceladaAActiva_debeRetornar409() throws Exception {
        when(cambiarEstadoCuentaUseCase.cambiarEstado(1L, EstadoCuenta.ACTIVA))
                .thenThrow(new TransicionEstadoInvalidaException(
                        "No se puede cambiar de CANCELADA a ACTIVA."));

        mockMvc.perform(patch("/api/cuentas/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nuevoEstado\":\"ACTIVA\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void cambiarEstado_aInactivaDesdeActiva_debeRetornar200() throws Exception {
        Cuenta inactiva = cuentaEjemplo(1L, EstadoCuenta.INACTIVA, BigDecimal.ZERO);
        when(cambiarEstadoCuentaUseCase.cambiarEstado(1L, EstadoCuenta.INACTIVA)).thenReturn(inactiva);

        mockMvc.perform(patch("/api/cuentas/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nuevoEstado\":\"INACTIVA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("INACTIVA"));
    }

    @Test
    void cambiarEstado_sinNuevoEstado_debeRetornar400() throws Exception {
        mockMvc.perform(patch("/api/cuentas/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}