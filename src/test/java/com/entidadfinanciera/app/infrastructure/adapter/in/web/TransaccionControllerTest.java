package com.entidadfinanciera.app.infrastructure.adapter.in.web;

import com.entidadfinanciera.app.application.port.in.*;
import com.entidadfinanciera.app.domain.exception.SaldoInsuficienteException;
import com.entidadfinanciera.app.domain.exception.TransferenciaMismaCuentaException;
import com.entidadfinanciera.app.domain.model.TipoTransaccion;
import com.entidadfinanciera.app.domain.model.Transaccion;
import com.entidadfinanciera.app.infrastructure.adapter.in.web.exception.GlobalExceptionHandler;
import com.entidadfinanciera.app.infrastructure.adapter.in.web.mapper.TransaccionWebMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransaccionController.class)
@Import({TransaccionWebMapper.class, GlobalExceptionHandler.class})
class TransaccionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RealizarConsignacionUseCase realizarConsignacionUseCase;

    @MockBean
    private RealizarRetiroUseCase realizarRetiroUseCase;

    @MockBean
    private RealizarTransferenciaUseCase realizarTransferenciaUseCase;

    @MockBean
    private ConsultarTransaccionUseCase consultarTransaccionUseCase;

    // --- Caso exitoso: consignación (201) ---
    @Test
    void consignar_conDatosValidos_debeRetornar201() throws Exception {
        // Constructor real de Transaccion: (id, tipoTransaccion, monto, descripcion, fechaCreacion, movimientos)
        Transaccion t = new Transaccion(
                1L,
                TipoTransaccion.CONSIGNACION,
                new BigDecimal("100000"),
                "Consignación de prueba",
                LocalDateTime.now(),
                List.of()
        );

        when(realizarConsignacionUseCase.consignar(any(Long.class), any(BigDecimal.class), any(String.class)))
                .thenReturn(t);

        mockMvc.perform(post("/api/transacciones/consignaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cuentaId\":1,\"monto\":100000,\"descripcion\":\"Consignación de prueba\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    // --- Regla de negocio: transferencia a la misma cuenta (400) ---
    @Test
    void transferir_aMismaCuenta_debeRetornar400() throws Exception {
        when(realizarTransferenciaUseCase.transferir(any(Long.class), any(Long.class), any(BigDecimal.class), any(String.class)))
                .thenThrow(new TransferenciaMismaCuentaException("No se puede realizar una transferencia a la misma cuenta."));

        mockMvc.perform(post("/api/transacciones/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cuentaOrigenId\":1,\"cuentaDestinoId\":1,\"monto\":50000,\"descripcion\":\"Transferencia de prueba\"}"))
                .andExpect(status().isBadRequest());
    }

    // --- Regla de negocio: saldo insuficiente (409) ---
    @Test
    void retirar_conSaldoInsuficiente_debeRetornar409() throws Exception {
        when(realizarRetiroUseCase.retirar(any(Long.class), any(BigDecimal.class), any(String.class)))
                .thenThrow(new SaldoInsuficienteException("Saldo insuficiente para realizar el retiro."));

        mockMvc.perform(post("/api/transacciones/retiros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cuentaId\":1,\"monto\":500000,\"descripcion\":\"Retiro de prueba\"}"))
                .andExpect(status().isConflict());
    }
}