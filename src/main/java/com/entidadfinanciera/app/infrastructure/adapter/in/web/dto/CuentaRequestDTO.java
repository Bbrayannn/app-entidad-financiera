package com.entidadfinanciera.app.infrastructure.adapter.in.web.dto;

import com.entidadfinanciera.app.domain.model.TipoCuenta;
import jakarta.validation.constraints.NotNull;

public record CuentaRequestDTO(
        @NotNull(message = "El cliente es obligatorio") Long clienteId,
        @NotNull(message = "El tipo de cuenta es obligatorio") TipoCuenta tipoCuenta,
        boolean exentaGmf
) {}