package com.entidadfinanciera.app.application.service;

import com.entidadfinanciera.app.application.port.in.ActualizarClienteUseCase;
import com.entidadfinanciera.app.application.port.in.ConsultarClienteUseCase;
import com.entidadfinanciera.app.application.port.in.CrearClienteUseCase;
import com.entidadfinanciera.app.application.port.in.EliminarClienteUseCase;
import com.entidadfinanciera.app.application.port.out.ClienteRepositoryPort;
import com.entidadfinanciera.app.domain.exception.*;
import com.entidadfinanciera.app.domain.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


/**
 * Orquesta los casos de uso del módulo Clientes. Depende únicamente de la interfaz
 * ClienteRepositoryPort, nunca de JPA directamente  así puedo testear toda la lógica
 * de negocio con un mock, sin levantar base de datos (ver ClienteServiceTest).
 */

@Service
public class ClienteService implements CrearClienteUseCase, ActualizarClienteUseCase,
        EliminarClienteUseCase, ConsultarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;

    public ClienteService(ClienteRepositoryPort clienteRepositoryPort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
    }

    /**
     * Valida mayoría de edad y unica de identificación/correo antes de guardar.
     * Verifico duplicados aquí, a nivel de aplicación, y también con UNIQUE en el DDL:
     * la doble capa evita condiciones de carrera si dos registros con el mismo dato
     * llegan casi al mismo tiempo .
     */
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

    /** Recalcula fechaModificacion en cada actualización, tal como lo exige el PDF. */

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

    /**
     * Un cliente con cuentas asociadas no puede eliminarse (regla explícita del PDF).
     * Consulto primero que exista, y luego que no tenga cuentas, en ese orden,
     * para poder distinguir 404 (no existe) de 409 (existe pero no se puede borrar).
     */


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

    /** Pagino el listado en vez de devolver todo de una vez, pensando en cuando la tabla crezca. */

    @Override
    public Page<Cliente> listarTodos(Pageable pageable) {
        return clienteRepositoryPort.listarTodos(pageable);
    }
}