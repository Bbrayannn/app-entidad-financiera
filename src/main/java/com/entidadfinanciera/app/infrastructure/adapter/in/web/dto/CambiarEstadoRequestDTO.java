package com.entidadfinanciera.app.infrastructure.adapter.in.web.dto;

import com.entidadfinanciera.app.domain.model.EstadoCuenta;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoRequestDTO(
        @NotNull(message = "El nuevo estado es obligatorio") EstadoCuenta nuevoEstado
) {}