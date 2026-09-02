package com.entidadfinanciera.app.application.service;

import com.entidadfinanciera.app.application.port.out.ClienteRepositoryPort;
import com.entidadfinanciera.app.domain.exception.*;
import com.entidadfinanciera.app.domain.model.Cliente;
import com.entidadfinanciera.app.domain.model.TipoIdentificacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente clienteValido;

    @BeforeEach
    void setUp() {
        clienteValido = new Cliente(null, TipoIdentificacion.CC, "1023456789",
                "Juan", "Pérez", "juan.perez@correo.com",
                LocalDate.of(1995, 4, 12), null, null);
    }

    // --- Caso exitoso ---
    @Test
    void crear_conDatosValidos_debeGuardarYRetornarCliente() {
        when(clienteRepositoryPort.existePorNumeroIdentificacion(anyString())).thenReturn(false);
        when(clienteRepositoryPort.existePorCorreo(anyString())).thenReturn(false);
        when(clienteRepositoryPort.guardar(any(Cliente.class))).thenAnswer(inv -> {
            Cliente c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        Cliente resultado = clienteService.crear(clienteValido);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getFechaCreacion()).isNotNull();
        verify(clienteRepositoryPort).guardar(any(Cliente.class));
    }

    // --- Regla de negocio incumplida: menor de edad ---
    @Test
    void crear_conClienteMenorDeEdad_debeLanzarExcepcion() {
        Cliente menor = new Cliente(null, TipoIdentificacion.CC, "1099999999",
                "Ana", "Ruiz", "ana@correo.com", LocalDate.now().minusYears(10), null, null);

        assertThatThrownBy(() -> clienteService.crear(menor))
                .isInstanceOf(ClienteMenorDeEdadException.class)
                .hasMessageContaining("menor de edad");

        verify(clienteRepositoryPort, never()).guardar(any());
    }

    // --- Caso límite: cumple exactamente 18 años hoy ---
    @Test
    void crear_conClienteQueCumple18HoyExacto_debePermitirCreacion() {
        Cliente cumpleanosHoy = new Cliente(null, TipoIdentificacion.CC, "1000000001",
                "Carlos", "Nuevo", "carlos@correo.com", LocalDate.now().minusYears(18), null, null);

        when(clienteRepositoryPort.existePorNumeroIdentificacion(anyString())).thenReturn(false);
        when(clienteRepositoryPort.existePorCorreo(anyString())).thenReturn(false);
        when(clienteRepositoryPort.guardar(any(Cliente.class))).thenReturn(cumpleanosHoy);

        assertThatCode(() -> clienteService.crear(cumpleanosHoy)).doesNotThrowAnyException();
    }

    // --- Dato inválido / duplicado: numero de identificacion ---
    @Test
    void crear_conNumeroIdentificacionDuplicado_debeLanzarExcepcion() {
        when(clienteRepositoryPort.existePorNumeroIdentificacion(anyString())).thenReturn(true);

        assertThatThrownBy(() -> clienteService.crear(clienteValido))
                .isInstanceOf(ClienteDuplicadoException.class);

        verify(clienteRepositoryPort, never()).guardar(any());
    }

    // --- Dato inválido / duplicado: correo ---
    @Test
    void crear_conCorreoDuplicado_debeLanzarExcepcion() {
        when(clienteRepositoryPort.existePorNumeroIdentificacion(anyString())).thenReturn(false);
        when(clienteRepositoryPort.existePorCorreo(anyString())).thenReturn(true);

        assertThatThrownBy(() -> clienteService.crear(clienteValido))
                .isInstanceOf(ClienteDuplicadoException.class);
    }

    // --- Recurso inexistente: actualizar ---
    @Test
    void actualizar_conIdInexistente_debeLanzarExcepcion() {
        when(clienteRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.actualizar(99L, clienteValido))
                .isInstanceOf(ClienteNoEncontradoException.class);
    }

    // --- Caso exitoso: actualizar recalcula fecha de modificación ---
    @Test
    void actualizar_conDatosValidos_debeActualizarFechaModificacion() {
        Cliente existente = new Cliente(1L, TipoIdentificacion.CC, "1023456789",
                "Juan", "Pérez", "juan@correo.com", LocalDate.of(1995, 4, 12),
                LocalDateTime.now().minusDays(10), null);

        when(clienteRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(clienteRepositoryPort.guardar(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        Cliente actualizado = clienteService.actualizar(1L, clienteValido);

        assertThat(actualizado.getFechaModificacion()).isNotNull();
        assertThat(actualizado.getFechaModificacion()).isAfter(actualizado.getFechaCreacion());
    }

    // --- Regla de negocio: no eliminar con productos vinculados ---
    @Test
    void eliminar_conCuentasVinculadas_debeLanzarExcepcion() {
        when(clienteRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(clienteValido));
        when(clienteRepositoryPort.tieneCuentasVinculadas(1L)).thenReturn(true);

        assertThatThrownBy(() -> clienteService.eliminar(1L))
                .isInstanceOf(ClienteConProductosVinculadosException.class);

        verify(clienteRepositoryPort, never()).eliminar(any());
    }

    // --- Caso exitoso: eliminar sin cuentas vinculadas ---
    @Test
    void eliminar_sinCuentasVinculadas_debeEliminarCorrectamente() {
        when(clienteRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(clienteValido));
        when(clienteRepositoryPort.tieneCuentasVinculadas(1L)).thenReturn(false);

        clienteService.eliminar(1L);

        verify(clienteRepositoryPort).eliminar(1L);
    }

    // --- Recurso inexistente: buscarPorId ---
    @Test
    void buscarPorId_conIdInexistente_debeLanzarExcepcion() {
        when(clienteRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.buscarPorId(99L))
                .isInstanceOf(ClienteNoEncontradoException.class);
    }

    // --- Caso exitoso: listar todos paginado ---
    @Test
    void listarTodos_debeRetornarPaginaDeClientes() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cliente> paginaEsperada = new PageImpl<>(List.of(clienteValido));

        when(clienteRepositoryPort.listarTodos(pageable)).thenReturn(paginaEsperada);

        Page<Cliente> resultado = clienteService.listarTodos(pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        verify(clienteRepositoryPort).listarTodos(pageable);
    }
}