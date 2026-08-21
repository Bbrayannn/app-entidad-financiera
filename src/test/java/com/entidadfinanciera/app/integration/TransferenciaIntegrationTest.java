package com.entidadfinanciera.app.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class TransferenciaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void flujoCompleto_crearClienteCuentasYTransferir_debeActualizarSaldosCorrectamente() throws Exception {
        // 1. Crear cliente
        String clienteJson = """
            {"tipoIdentificacion":"CC","numeroIdentificacion":"1000000001","nombres":"Laura",
             "apellido":"Gómez","correoElectronico":"laura@correo.com","fechaNacimiento":"1990-05-01"}
            """;
        String respuestaCliente = mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON).content(clienteJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long clienteId = ((Number) objectMapper.readValue(respuestaCliente, Map.class).get("id")).longValue();

        // 2. Crear cuenta origen (ahorros)
        String cuentaOrigenJson = "{\"clienteId\":" + clienteId + ",\"tipoCuenta\":\"AHORROS\",\"exentaGmf\":false}";
        String respCuentaOrigen = mockMvc.perform(post("/api/cuentas")
                        .contentType(MediaType.APPLICATION_JSON).content(cuentaOrigenJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long cuentaOrigenId = ((Number) objectMapper.readValue(respCuentaOrigen, Map.class).get("id")).longValue();

        // 3. Crear cuenta destino
        String cuentaDestinoJson = "{\"clienteId\":" + clienteId + ",\"tipoCuenta\":\"CORRIENTE\",\"exentaGmf\":false}";
        String respCuentaDestino = mockMvc.perform(post("/api/cuentas")
                        .contentType(MediaType.APPLICATION_JSON).content(cuentaDestinoJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long cuentaDestinoId = ((Number) objectMapper.readValue(respCuentaDestino, Map.class).get("id")).longValue();

        // 4. Consignar 100.000 a la cuenta origen
        String consignacionJson = "{\"cuentaId\":" + cuentaOrigenId + ",\"monto\":100000,\"descripcion\":\"Depósito inicial\"}";
        mockMvc.perform(post("/api/transacciones/consignaciones")
                        .contentType(MediaType.APPLICATION_JSON).content(consignacionJson))
                .andExpect(status().isCreated());

        // 5. Transferir 40.000 de origen a destino
        String transferenciaJson = "{\"cuentaOrigenId\":" + cuentaOrigenId + ",\"cuentaDestinoId\":" + cuentaDestinoId +
                ",\"monto\":40000,\"descripcion\":\"Pago\"}";
        mockMvc.perform(post("/api/transacciones/transferencias")
                        .contentType(MediaType.APPLICATION_JSON).content(transferenciaJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.movimientos.length()").value(2));

        // 6. Verificar saldos finales reales en la base de datos (vía el endpoint)
        mockMvc.perform(get("/api/cuentas/" + cuentaOrigenId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(60000));

        mockMvc.perform(get("/api/cuentas/" + cuentaDestinoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(40000));
    }
}