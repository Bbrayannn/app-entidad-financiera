package com.entidadfinanciera.app.infrastructure.adapter.out.persistence.mapper;

import com.entidadfinanciera.app.domain.model.Cuenta;
import com.entidadfinanciera.app.infrastructure.adapter.out.persistence.entity.CuentaEntity;
import org.springframework.stereotype.Component;

@Component
public class CuentaEntityMapper {

    public CuentaEntity aEntity(Cuenta cuenta) {
        return new CuentaEntity(
                cuenta.getId(), cuenta.getTipoCuenta(), cuenta.getNumeroCuenta(),
                cuenta.getEstado(), cuenta.getSaldo(), cuenta.isExentaGmf(),
                cuenta.getFechaCreacion(), cuenta.getFechaModificacion(),
                cuenta.getClienteId(), cuenta.getVersion()
        );
    }

    public Cuenta aDominio(CuentaEntity entity) {
        return new Cuenta(
                entity.getId(), entity.getTipoCuenta(), entity.getNumeroCuenta(),
                entity.getEstado(), entity.getSaldo(), entity.isExentaGmf(),
                entity.getFechaCreacion(), entity.getFechaModificacion(),
                entity.getClienteId(), entity.getVersion()
        );
    }
}