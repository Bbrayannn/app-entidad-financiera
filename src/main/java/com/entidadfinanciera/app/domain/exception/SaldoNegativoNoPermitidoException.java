package com.entidadfinanciera.app.domain.exception;

public class SaldoNegativoNoPermitidoException extends RuntimeException {
    public SaldoNegativoNoPermitidoException(String mensaje) { super(mensaje); }
}