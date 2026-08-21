package com.entidadfinanciera.app.infrastructure.adapter.in.web.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponseDTO(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String path,
        List<String> detalles
) {
    public ErrorResponseDTO(int status, String error, String mensaje, String path) {
        this(LocalDateTime.now(), status, error, mensaje, path, null);
    }

    public ErrorResponseDTO(int status, String error, String mensaje, String path, List<String> detalles) {
        this(LocalDateTime.now(), status, error, mensaje, path, detalles);
    }
}