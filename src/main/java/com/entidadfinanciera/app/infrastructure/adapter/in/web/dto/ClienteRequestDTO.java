package com.entidadfinanciera.app.infrastructure.adapter.in.web.dto;

import com.entidadfinanciera.app.domain.model.TipoIdentificacion;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record ClienteRequestDTO(
        @NotNull(message = "El tipo de identificación es obligatorio")
        TipoIdentificacion tipoIdentificacion,

        @NotBlank(message = "El número de identificación es obligatorio")
        @Pattern(regexp = "^[0-9]{5,15}$", message = "El número de identificación debe tener entre 5 y 15 dígitos numéricos")
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