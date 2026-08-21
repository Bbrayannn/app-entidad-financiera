package com.entidadfinanciera.app.domain.exception;

public class MontoInvalidoException extends RuntimeException {
    public MontoInvalidoException(String mensaje) { super(mensaje); }
}