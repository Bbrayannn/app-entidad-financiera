package com.entidadfinanciera.app.infrastructure.adapter.in.web.dto;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje
) {
    public ErrorResponseDTO(int status, String error, String mensaje) {
        this(LocalDateTime.now(), status, error, mensaje);
    }
}