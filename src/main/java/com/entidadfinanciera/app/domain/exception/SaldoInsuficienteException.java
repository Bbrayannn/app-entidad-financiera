package com.entidadfinanciera.app.domain.exception;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(String mensaje) { super(mensaje); }
}