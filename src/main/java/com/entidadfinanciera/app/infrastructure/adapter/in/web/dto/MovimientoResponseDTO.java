package com.entidadfinanciera.app.infrastructure.adapter.in.web.dto;

import com.entidadfinanciera.app.domain.model.TipoMovimiento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoResponseDTO(
        Long id,
        Long cuentaId,
        TipoMovimiento tipoMovimiento,
        BigDecimal monto,
        BigDecimal saldoResultante,
        LocalDateTime fechaCreacion
) {}