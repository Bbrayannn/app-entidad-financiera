package com.entidadfinanciera.app.domain.exception;

public class CuentaNoCanceladaException extends RuntimeException {
    public CuentaNoCanceladaException(String mensaje) {
        super(mensaje);
    }
}