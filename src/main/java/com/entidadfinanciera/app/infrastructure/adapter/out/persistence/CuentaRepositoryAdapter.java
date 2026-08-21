package com.entidadfinanciera.app.infrastructure.adapter.out.persistence;

import com.entidadfinanciera.app.application.port.out.CuentaRepositoryPort;
import com.entidadfinanciera.app.domain.model.Cuenta;
import com.entidadfinanciera.app.infrastructure.adapter.out.persistence.mapper.CuentaEntityMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CuentaRepositoryAdapter implements CuentaRepositoryPort {

    private final CuentaJpaRepository jpaRepository;
    private final CuentaEntityMapper mapper;

    public CuentaRepositoryAdapter(CuentaJpaRepository jpaRepository, CuentaEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Cuenta guardar(Cuenta cuenta) {
        return mapper.aDominio(jpaRepository.save(mapper.aEntity(cuenta)));
    }

    @Override
    public Optional<Cuenta> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::aDominio);
    }

    @Override
    public List<Cuenta> listarPorCliente(Long clienteId) {
        return jpaRepository.findByClienteId(clienteId).stream().map(mapper::aDominio).toList();
    }

    @Override
    public boolean existePorNumeroCuenta(String numeroCuenta) {
        return jpaRepository.existsByNumeroCuenta(numeroCuenta);
    }

    @Override
    public boolean existenCuentasParaCliente(Long clienteId) {
        return jpaRepository.existsByClienteId(clienteId);
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }
}