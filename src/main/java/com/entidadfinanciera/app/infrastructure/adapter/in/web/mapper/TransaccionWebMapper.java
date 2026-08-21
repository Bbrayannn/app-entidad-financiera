package com.entidadfinanciera.app.infrastructure.adapter.in.web.mapper;

import com.entidadfinanciera.app.domain.model.Transaccion;
import com.entidadfinanciera.app.infrastructure.adapter.in.web.dto.MovimientoResponseDTO;
import com.entidadfinanciera.app.infrastructure.adapter.in.web.dto.TransaccionResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class TransaccionWebMapper {

    public TransaccionResponseDTO aResponseDTO(Transaccion transaccion) {
        var movimientos = transaccion.getMovimientos().stream()
                .map(m -> new MovimientoResponseDTO(m.getId(), m.getCuentaId(), m.getTipoMovimiento(),
                        m.getMonto(), m.getSaldoResultante(), m.getFechaCreacion()))
                .toList();
        return new TransaccionResponseDTO(transaccion.getId(), transaccion.getTipoTransaccion(),
                transaccion.getMonto(), transaccion.getDescripcion(), transaccion.getFechaCreacion(), movimientos);
    }
}