package com.entidadfinanciera.app.infrastructure.adapter.out.persistence.mapper;

import com.entidadfinanciera.app.domain.model.Movimiento;
import com.entidadfinanciera.app.domain.model.Transaccion;
import com.entidadfinanciera.app.infrastructure.adapter.out.persistence.entity.MovimientoEntity;
import com.entidadfinanciera.app.infrastructure.adapter.out.persistence.entity.TransaccionEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransaccionEntityMapper {

    public TransaccionEntity aEntity(Transaccion transaccion) {
        TransaccionEntity entity = new TransaccionEntity(
                transaccion.getId(), transaccion.getTipoTransaccion(), transaccion.getMonto(),
                transaccion.getDescripcion(), transaccion.getFechaCreacion()
        );
        List<MovimientoEntity> movimientos = transaccion.getMovimientos().stream()
                .map(m -> new MovimientoEntity(null, entity, m.getCuentaId(), m.getTipoMovimiento(),
                        m.getMonto(), m.getSaldoResultante(), m.getFechaCreacion()))
                .toList();
        entity.setMovimientos(movimientos);
        return entity;
    }

    public Transaccion aDominio(TransaccionEntity entity) {
        List<Movimiento> movimientos = entity.getMovimientos().stream()
                .map(m -> new Movimiento(m.getId(), entity.getId(), m.getCuentaId(), m.getTipoMovimiento(),
                        m.getMonto(), m.getSaldoResultante(), m.getFechaCreacion()))
                .toList();
        return new Transaccion(entity.getId(), entity.getTipoTransaccion(), entity.getMonto(),
                entity.getDescripcion(), entity.getFechaCreacion(), movimientos);
    }
}