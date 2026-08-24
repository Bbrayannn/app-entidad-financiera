package com.entidadfinanciera.app.domain.exception;

public class ConcurrenciaSaldoException extends RuntimeException {
    public ConcurrenciaSaldoException(String mensaje) { super(mensaje); }
}