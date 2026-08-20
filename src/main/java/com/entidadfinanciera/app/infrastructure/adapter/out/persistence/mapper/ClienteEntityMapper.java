package com.entidadfinanciera.app.infrastructure.adapter.out.persistence.mapper;

import com.entidadfinanciera.app.domain.model.Cliente;
import com.entidadfinanciera.app.infrastructure.adapter.out.persistence.entity.ClienteEntity;
import org.springframework.stereotype.Component;

@Component
public class ClienteEntityMapper {

    public ClienteEntity aEntity(Cliente cliente) {
        return new ClienteEntity(
                cliente.getId(),
                cliente.getTipoIdentificacion(),
                cliente.getNumeroIdentificacion(),
                cliente.getNombres(),
                cliente.getApellido(),
                cliente.getCorreoElectronico(),
                cliente.getFechaNacimiento(),
                cliente.getFechaCreacion(),
                cliente.getFechaModificacion()
        );
    }

    public Cliente aDominio(ClienteEntity entity) {
        return new Cliente(
                entity.getId(),
                entity.getTipoIdentificacion(),
                entity.getNumeroIdentificacion(),
                entity.getNombres(),
                entity.getApellido(),
                entity.getCorreoElectronico(),
                entity.getFechaNacimiento(),
                entity.getFechaCreacion(),
                entity.getFechaModificacion()
        );
    }
}