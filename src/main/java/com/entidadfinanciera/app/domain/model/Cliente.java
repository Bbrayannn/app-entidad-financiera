package com.entidadfinanciera.app.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;


/**
 * Cliente de la entidad financiera es solo  de dominio puro: no tiene anotaciones
 * de Spring ni de JPA porque no debe conocer cómo se persiste ni cómo se expone por HTTP.
 * La única regla  que vive aquí es que el cleinte tiene que ser mayor de edad, porque depende
 * únicamente de un dato propio del cliente (su fecha de nacimiento).
 */
public class Cliente {

    private Long id;
    private TipoIdentificacion tipoIdentificacion;
    private String numeroIdentificacion;
    private String nombres;
    private String apellido;
    private String correoElectronico;
    private LocalDate fechaNacimiento;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    public Cliente(Long id, TipoIdentificacion tipoIdentificacion, String numeroIdentificacion,
                   String nombres, String apellido, String correoElectronico,
                   LocalDate fechaNacimiento, LocalDateTime fechaCreacion,
                   LocalDateTime fechaModificacion) {
        this.id = id;
        this.tipoIdentificacion = tipoIdentificacion;
        this.numeroIdentificacion = numeroIdentificacion;
        this.nombres = nombres;
        this.apellido = apellido;
        this.correoElectronico = correoElectronico;
        this.fechaNacimiento = fechaNacimiento;
        this.fechaCreacion = fechaCreacion;
        this.fechaModificacion = fechaModificacion;
    }

    /**
     * Determina si el cliente es mayor de edad a la fecha actual.
     * S un cliente menor de edad no puede ser registrado en el sistema.
     *
     * @return true si tiene 18 años o más
     */

    public boolean esMayorDeEdad() {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears() >= 18;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TipoIdentificacion getTipoIdentificacion() { return tipoIdentificacion; }
    public void setTipoIdentificacion(TipoIdentificacion tipoIdentificacion) { this.tipoIdentificacion = tipoIdentificacion; }

    public String getNumeroIdentificacion() { return numeroIdentificacion; }
    public void setNumeroIdentificacion(String numeroIdentificacion) { this.numeroIdentificacion = numeroIdentificacion; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }
}