package com.entidadfinanciera.app.domain.exception;

public class CuentaNoEncontradaException extends RuntimeException {
    public CuentaNoEncontradaException(String mensaje) { super(mensaje); }
}