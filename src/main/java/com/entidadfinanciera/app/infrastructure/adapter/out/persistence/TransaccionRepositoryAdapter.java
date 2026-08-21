package com.entidadfinanciera.app.infrastructure.adapter.out.persistence;

import com.entidadfinanciera.app.application.port.out.TransaccionRepositoryPort;
import com.entidadfinanciera.app.domain.model.Transaccion;
import com.entidadfinanciera.app.infrastructure.adapter.out.persistence.entity.MovimientoEntity;
import com.entidadfinanciera.app.infrastructure.adapter.out.persistence.mapper.TransaccionEntityMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TransaccionRepositoryAdapter implements TransaccionRepositoryPort {

    private final TransaccionJpaRepository transaccionJpaRepository;
    private final MovimientoJpaRepository movimientoJpaRepository;
    private final TransaccionEntityMapper mapper;

    public TransaccionRepositoryAdapter(TransaccionJpaRepository transaccionJpaRepository,
                                        MovimientoJpaRepository movimientoJpaRepository,
                                        TransaccionEntityMapper mapper) {
        this.transaccionJpaRepository = transaccionJpaRepository;
        this.movimientoJpaRepository = movimientoJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Transaccion guardar(Transaccion transaccion) {
        // Gracias a cascade = CascadeType.ALL en TransaccionEntity.movimientos,
        // este único save() persiste la transacción Y sus movimientos en la misma operación.
        var guardada = transaccionJpaRepository.save(mapper.aEntity(transaccion));
        return mapper.aDominio(guardada);
    }

    @Override
    public Optional<Transaccion> buscarPorId(Long id) {
        return transaccionJpaRepository.findById(id).map(mapper::aDominio);
    }

    @Override
    public List<Transaccion> listarPorCuenta(Long cuentaId) {
        // Consultamos movimientos por cuenta y agrupamos por transacción para reconstruir el historial
        List<MovimientoEntity> movimientos = movimientoJpaRepository.findByCuentaIdOrderByFechaCreacionDesc(cuentaId);
        return movimientos.stream()
                .map(m -> m.getTransaccion().getId())
                .distinct()
                .map(id -> transaccionJpaRepository.findById(id).map(mapper::aDominio).orElseThrow())
                .toList();
    }
}