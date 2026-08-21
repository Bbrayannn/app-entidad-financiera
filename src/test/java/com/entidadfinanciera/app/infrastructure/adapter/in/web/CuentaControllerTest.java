package com.entidadfinanciera.app.infrastructure.adapter.in.web;

import com.entidadfinanciera.app.application.port.in.*;
import com.entidadfinanciera.app.domain.exception.SaldoInvalidoParaCancelarException;
import com.entidadfinanciera.app.domain.model.*;
import com.entidadfinanciera.app.infrastructure.adapter.in.web.exception.GlobalExceptionHandler;
import com.entidadfinanciera.app.infrastructure.adapter.in.web.mapper.CuentaWebMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
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
    @MockBean
    private EliminarCuentaUseCase eliminarCuentaUseCase;

    @Test
    void crear_conDatosValidos_debeRetornar201() throws Exception {
        Cuenta creada = new Cuenta(1L, TipoCuenta.AHORROS, "5300000001", EstadoCuenta.ACTIVA,
                BigDecimal.ZERO, false, LocalDateTime.now(), null, 1L);
        when(crearCuentaUseCase.crear(1L, TipoCuenta.AHORROS, false)).thenReturn(creada);

        mockMvc.perform(post("/api/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clienteId\":1,\"tipoCuenta\":\"AHORROS\",\"exentaGmf\":false}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCuenta").value("5300000001"));
    }

    @Test
    void cambiarEstado_aCanceladaConSaldoPositivo_debeRetornar409() throws Exception {
        when(cambiarEstadoCuentaUseCase.cambiarEstado(1L, EstadoCuenta.CANCELADA))
                .thenThrow(new SaldoInvalidoParaCancelarException("Solo se pueden cancelar cuentas con saldo igual a $0."));

        mockMvc.perform(patch("/api/cuentas/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nuevoEstado\":\"CANCELADA\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void crear_sinClienteId_debeRetornar400() throws Exception {
        mockMvc.perform(post("/api/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipoCuenta\":\"AHORROS\",\"exentaGmf\":false}"))
                .andExpect(status().isBadRequest());
    }
}