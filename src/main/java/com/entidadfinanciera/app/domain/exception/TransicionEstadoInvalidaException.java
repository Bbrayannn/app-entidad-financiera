package com.entidadfinanciera.app.domain.exception;

public class TransicionEstadoInvalidaException extends RuntimeException {
    public TransicionEstadoInvalidaException(String mensaje) { super(mensaje); }
}