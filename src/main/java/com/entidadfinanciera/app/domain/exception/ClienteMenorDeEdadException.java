package com.entidadfinanciera.app.domain.exception;

public class ClienteMenorDeEdadException extends RuntimeException {
    public ClienteMenorDeEdadException(String mensaje) {
        super(mensaje);
    }
}