package com.entidadfinanciera.app.infrastructure.adapter.out.persistence;

import com.entidadfinanciera.app.application.port.out.CuentaRepositoryPort;
import com.entidadfinanciera.app.domain.exception.ConcurrenciaSaldoException;
import com.entidadfinanciera.app.domain.exception.CuentaNoEncontradaException;
import com.entidadfinanciera.app.domain.model.Cuenta;
import com.entidadfinanciera.app.infrastructure.adapter.out.persistence.entity.CuentaEntity;
import com.entidadfinanciera.app.infrastructure.adapter.out.persistence.mapper.CuentaEntityMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    public Page<Cuenta> listarPorCliente(Long clienteId, Pageable pageable) {
        return jpaRepository.findByClienteId(clienteId, pageable).map(mapper::aDominio);
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
    public void actualizarSaldo(Long cuentaId, BigDecimal nuevoSaldo, LocalDateTime fechaModificacion) {
        int intentos = 0;
        while (intentos < 3) {
            try {
                CuentaEntity entity = jpaRepository.findById(cuentaId)
                        .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada: " + cuentaId));
                entity.setSaldo(nuevoSaldo);
                entity.setFechaModificacion(fechaModificacion);
                jpaRepository.saveAndFlush(entity);
                return;
            } catch (ObjectOptimisticLockingFailureException ex) {
                intentos++;
                if (intentos == 3) {
                    throw new ConcurrenciaSaldoException(
                            "No fue posible actualizar el saldo tras varios intentos por alta concurrencia sobre la cuenta " + cuentaId);
                }
            }
        }
    }
}