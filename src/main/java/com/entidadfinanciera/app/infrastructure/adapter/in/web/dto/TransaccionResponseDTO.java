package com.entidadfinanciera.app.infrastructure.adapter.in.web.dto;

import com.entidadfinanciera.app.domain.model.TipoTransaccion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record TransaccionResponseDTO(
        Long id,
        TipoTransaccion tipoTransaccion,
        BigDecimal monto,
        String descripcion,
        LocalDateTime fechaCreacion,
        List<MovimientoResponseDTO> movimientos
) {}