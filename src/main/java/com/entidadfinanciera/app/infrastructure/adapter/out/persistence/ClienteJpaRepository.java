package com.entidadfinanciera.app.infrastructure.adapter.out.persistence;

import com.entidadfinanciera.app.infrastructure.adapter.out.persistence.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, Long> {
    boolean existsByNumeroIdentificacion(String numeroIdentificacion);
    boolean existsByCorreoElectronico(String correoElectronico);
    Optional<ClienteEntity> findByNumeroIdentificacion(String numeroIdentificacion);
}