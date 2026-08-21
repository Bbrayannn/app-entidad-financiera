package com.entidadfinanciera.app.infrastructure.adapter.in.web.dto;

import com.entidadfinanciera.app.domain.model.EstadoCuenta;
import com.entidadfinanciera.app.domain.model.TipoCuenta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CuentaResponseDTO(
        Long id,
        TipoCuenta tipoCuenta,
        String numeroCuenta,
        EstadoCuenta estado,
        BigDecimal saldo,
        boolean exentaGmf,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaModificacion,
        Long clienteId
) {}