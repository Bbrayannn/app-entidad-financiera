package com.entidadfinanciera.app.infrastructure.adapter.in.web;

import com.entidadfinanciera.app.application.port.in.*;
import com.entidadfinanciera.app.domain.exception.ClienteMenorDeEdadException;
import com.entidadfinanciera.app.domain.exception.ClienteNoEncontradoException;
import com.entidadfinanciera.app.domain.model.Cliente;
import com.entidadfinanciera.app.domain.model.TipoIdentificacion;
import com.entidadfinanciera.app.infrastructure.adapter.in.web.exception.GlobalExceptionHandler;
import com.entidadfinanciera.app.infrastructure.adapter.in.web.mapper.ClienteWebMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
@Import({ClienteWebMapper.class, GlobalExceptionHandler.class})
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CrearClienteUseCase crearClienteUseCase;
    @MockBean
    private ActualizarClienteUseCase actualizarClienteUseCase;
    @MockBean
    private EliminarClienteUseCase eliminarClienteUseCase;
    @MockBean
    private ConsultarClienteUseCase consultarClienteUseCase;

    // --- Caso exitoso: crear ---
    @Test
    void crear_conDatosValidos_debeRetornar201() throws Exception {
        var request = """
            {
              "tipoIdentificacion":"CC",
              "numeroIdentificacion":"1023456789",
              "nombres":"Juan",
              "apellido":"Pérez",
              "correoElectronico":"juan.perez@correo.com",
              "fechaNacimiento":"1995-04-12"
            }
            """;

        Cliente creado = new Cliente(1L, TipoIdentificacion.CC, "1023456789",
                "Juan", "Pérez", "juan.perez@correo.com", LocalDate.of(1995, 4, 12), null, null);

        when(crearClienteUseCase.crear(any(Cliente.class))).thenReturn(creado);

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombres").value("Juan"));
    }

    // --- Dato inválido: correo mal formado ---
    @Test
    void crear_conCorreoInvalido_debeRetornar400() throws Exception {
        var request = """
            {
              "tipoIdentificacion":"CC",
              "numeroIdentificacion":"1023456789",
              "nombres":"Juan",
              "apellido":"Pérez",
              "correoElectronico":"correo-invalido",
              "fechaNacimiento":"1995-04-12"
            }
            """;

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles").isArray());
    }

    // --- Dato inválido: nombre muy corto ---
    @Test
    void crear_conNombreMuyCorto_debeRetornar400() throws Exception {
        var request = """
            {
              "tipoIdentificacion":"CC",
              "numeroIdentificacion":"1023456789",
              "nombres":"J",
              "apellido":"Pérez",
              "correoElectronico":"juan@correo.com",
              "fechaNacimiento":"1995-04-12"
            }
            """;

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    // --- Regla de negocio incumplida propagada como 400 ---
    @Test
    void crear_conClienteMenorDeEdad_debeRetornar400() throws Exception {
        var request = """
            {
              "tipoIdentificacion":"CC",
              "numeroIdentificacion":"1099999999",
              "nombres":"Ana",
              "apellido":"Ruiz",
              "correoElectronico":"ana@correo.com",
              "fechaNacimiento":"2015-01-01"
            }
            """;

        when(crearClienteUseCase.crear(any(Cliente.class)))
                .thenThrow(new ClienteMenorDeEdadException("No se puede registrar un cliente menor de edad."));

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("No se puede registrar un cliente menor de edad."));
    }

    // --- Recurso inexistente ---
    @Test
    void buscarPorId_conIdInexistente_debeRetornar404() throws Exception {
        when(consultarClienteUseCase.buscarPorId(99L))
                .thenThrow(new ClienteNoEncontradoException("Cliente no encontrado con id 99"));

        mockMvc.perform(get("/api/clientes/99"))
                .andExpect(status().isNotFound());
    }

    // --- Caso exitoso: consultar ---
    @Test
    void buscarPorId_conIdExistente_debeRetornar200() throws Exception {
        Cliente cliente = new Cliente(1L, TipoIdentificacion.CC, "1023456789",
                "Juan", "Pérez", "juan@correo.com", LocalDate.of(1995, 4, 12), null, null);
        when(consultarClienteUseCase.buscarPorId(1L)).thenReturn(cliente);

        mockMvc.perform(get("/api/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombres").value("Juan"));
    }

    // --- Caso exitoso: eliminar ---
    @Test
    void eliminar_conIdExistente_debeRetornar204() throws Exception {
        mockMvc.perform(delete("/api/clientes/1"))
                .andExpect(status().isNoContent());
    }
}