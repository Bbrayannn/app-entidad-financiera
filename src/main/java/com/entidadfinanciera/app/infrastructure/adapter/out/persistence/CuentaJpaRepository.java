package com.entidadfinanciera.app.infrastructure.adapter.out.persistence;

import com.entidadfinanciera.app.infrastructure.adapter.out.persistence.entity.CuentaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaJpaRepository extends JpaRepository<CuentaEntity, Long> {
    boolean existsByNumeroCuenta(String numeroCuenta);
    boolean existsByClienteId(Long clienteId);
    Page<CuentaEntity> findByClienteId(Long clienteId, Pageable pageable);
}