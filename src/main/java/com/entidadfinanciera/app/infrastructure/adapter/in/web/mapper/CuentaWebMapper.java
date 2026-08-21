package com.entidadfinanciera.app.infrastructure.adapter.in.web.mapper;

import com.entidadfinanciera.app.domain.model.Cuenta;
import com.entidadfinanciera.app.infrastructure.adapter.in.web.dto.CuentaResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class CuentaWebMapper {

    public CuentaResponseDTO aResponseDTO(Cuenta cuenta) {
        return new CuentaResponseDTO(
                cuenta.getId(), cuenta.getTipoCuenta(), cuenta.getNumeroCuenta(),
                cuenta.getEstado(), cuenta.getSaldo(), cuenta.isExentaGmf(),
                cuenta.getFechaCreacion(), cuenta.getFechaModificacion(), cuenta.getClienteId()
        );
    }
}