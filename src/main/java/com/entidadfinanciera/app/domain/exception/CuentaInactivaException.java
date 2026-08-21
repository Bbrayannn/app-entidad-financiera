package com.entidadfinanciera.app.domain.exception;

public class CuentaInactivaException extends RuntimeException {
    public CuentaInactivaException(String mensaje) { super(mensaje); }
}