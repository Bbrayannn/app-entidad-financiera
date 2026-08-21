package com.entidadfinanciera.app.infrastructure.adapter.out.persistence;

import com.entidadfinanciera.app.infrastructure.adapter.out.persistence.entity.CuentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CuentaJpaRepository extends JpaRepository<CuentaEntity, Long> {
    boolean existsByNumeroCuenta(String numeroCuenta);
    boolean existsByClienteId(Long clienteId);
    List<CuentaEntity> findByClienteId(Long clienteId);
}