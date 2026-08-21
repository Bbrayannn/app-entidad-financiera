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
    private final CuentaJpaRepository cuentaJpaRepository;
    private final ClienteEntityMapper mapper;

    public ClienteRepositoryAdapter(ClienteJpaRepository jpaRepository,
                                    CuentaJpaRepository cuentaJpaRepository,
                                    ClienteEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.cuentaJpaRepository = cuentaJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        return mapper.aDominio(jpaRepository.save(mapper.aEntity(cliente)));
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
        return cuentaJpaRepository.existsByClienteId(clienteId);
    }
}