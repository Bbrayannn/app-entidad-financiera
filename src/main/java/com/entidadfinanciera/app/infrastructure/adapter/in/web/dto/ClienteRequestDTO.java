package com.entidadfinanciera.app.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record ClienteRequestDTO(
        @NotBlank(message = "El tipo de identificación es obligatorio")
        String tipoIdentificacion,

        @NotBlank(message = "El número de identificación es obligatorio")
        String numeroIdentificacion,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, message = "El nombre debe tener al menos 2 caracteres")
        String nombres,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(min = 2, message = "El apellido debe tener al menos 2 caracteres")
        String apellido,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo debe tener un formato válido")
        String correoElectronico,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser en el pasado")
        LocalDate fechaNacimiento
) {}