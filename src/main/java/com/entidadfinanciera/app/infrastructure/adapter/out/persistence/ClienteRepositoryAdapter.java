package com.entidadfinanciera.app.infrastructure.adapter.out.persistence;

import com.entidadfinanciera.app.application.port.out.ClienteRepositoryPort;
import com.entidadfinanciera.app.domain.model.Cliente;
import com.entidadfinanciera.app.infrastructure.adapter.out.persistence.mapper.ClienteEntityMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository jpaRepository;
    private final ClienteEntityMapper mapper;

    public ClienteRepositoryAdapter(ClienteJpaRepository jpaRepository, ClienteEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        var entityGuardada = jpaRepository.save(mapper.aEntity(cliente));
        return mapper.aDominio(entityGuardada);
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::aDominio);
    }

    @Override
    public List<Cliente> listarTodos() {
        return jpaRepository.findAll().stream().map(mapper::aDominio).toList();
    }

    @Override
    public boolean existePorNumeroIdentificacion(String numeroIdentificacion) {
        return jpaRepository.existsByNumeroIdentificacion(numeroIdentificacion);
    }

    @Override
    public boolean existePorCorreo(String correo) {
        return jpaRepository.existsByCorreoElectronico(correo);
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean tieneCuentasVinculadas(Long clienteId) {
        // Placeholder temporal: hasta que exista la tabla/entidad `cuentas` (Fase 7),
        // devolvemos false para no romper la compilación del módulo Clientes.
        // En la Fase 7 inyectaremos CuentaJpaRepository y consultaremos de verdad.
        return false;
    }
}