package com.entidadfinanciera.app.application.service;

import com.entidadfinanciera.app.application.port.in.ActualizarClienteUseCase;
import com.entidadfinanciera.app.application.port.in.ConsultarClienteUseCase;
import com.entidadfinanciera.app.application.port.in.CrearClienteUseCase;
import com.entidadfinanciera.app.application.port.in.EliminarClienteUseCase;
// IMPORTANTE: Asegúrate de tener este import
import com.entidadfinanciera.app.application.port.out.ClienteRepositoryPort;
import com.entidadfinanciera.app.domain.exception.*;
import com.entidadfinanciera.app.domain.model.Cliente;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClienteService implements CrearClienteUseCase, ActualizarClienteUseCase,
        EliminarClienteUseCase, ConsultarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;

    public ClienteService(ClienteRepositoryPort clienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    @Override
    @Transactional
    public Cliente crear(Cliente cliente) {
        if (!cliente.esMayorDeEdad()) {
            throw new ClienteMenorDeEdadException(
                    "No se puede registrar un cliente menor de edad.");
        }
        if (clienteRepositoryPort.existePorNumeroIdentificacion(cliente.getNumeroIdentificacion())) {
            throw new ClienteDuplicadoException(
                    "Ya existe un cliente con el número de identificación " + cliente.getNumeroIdentificacion());
        }
        if (clienteRepositoryPort.existePorCorreo(cliente.getCorreoElectronico())) {
            throw new ClienteDuplicadoException(
                    "Ya existe un cliente con el correo " + cliente.getCorreoElectronico());
        }
        cliente.setFechaCreacion(LocalDateTime.now());
        cliente.setFechaModificacion(null);
        return clienteRepositoryPort.guardar(cliente);
    }

    @Override
    @Transactional
    public Cliente actualizar(Long id, Cliente datosActualizados) {
        Cliente clienteExistente = clienteRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado con id " + id));

        clienteExistente.setNombres(datosActualizados.getNombres());
        clienteExistente.setApellido(datosActualizados.getApellido());
        clienteExistente.setCorreoElectronico(datosActualizados.getCorreoElectronico());
        clienteExistente.setFechaModificacion(LocalDateTime.now());

        return clienteRepositoryPort.guardar(clienteExistente);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        clienteRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado con id " + id));

        if (clienteRepositoryPort.tieneCuentasVinculadas(id)) {
            throw new ClienteConProductosVinculadosException(
                    "No se puede eliminar un cliente con productos vinculados.");
        }
        clienteRepositoryPort.eliminar(id);
    }

    @Override
    public Cliente buscarPorId(Long id) {
        return clienteRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado con id " + id));
    }

    @Override
    public List<Cliente> listarTodos() {
        return clienteRepositoryPort.listarTodos();
    }
}