package com.entidadfinanciera.app.infrastructure.adapter.in.web.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClienteResponseDTO(
        Long id,
        String tipoIdentificacion,
        String numeroIdentificacion,
        String nombres,
        String apellido,
        String correoElectronico,
        LocalDate fechaNacimiento,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaModificacion
) {}