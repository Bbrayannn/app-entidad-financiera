package com.entidadfinanciera.app.infrastructure.adapter.in.web.mapper;

import com.entidadfinanciera.app.domain.model.Cliente;
import com.entidadfinanciera.app.infrastructure.adapter.in.web.dto.ClienteRequestDTO;
import com.entidadfinanciera.app.infrastructure.adapter.in.web.dto.ClienteResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ClienteWebMapper {

    public Cliente aDominio(ClienteRequestDTO dto) {
        return new Cliente(
                null,
                dto.tipoIdentificacion(),
                dto.numeroIdentificacion(),
                dto.nombres(),
                dto.apellido(),
                dto.correoElectronico(),
                dto.fechaNacimiento(),
                null,
                null
        );
    }

    public ClienteResponseDTO aResponseDTO(Cliente cliente) {
        return new ClienteResponseDTO(
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
}