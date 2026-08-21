package com.entidadfinanciera.app.domain.exception;

public class SaldoInvalidoParaCancelarException extends RuntimeException {
    public SaldoInvalidoParaCancelarException(String mensaje) { super(mensaje); }
}