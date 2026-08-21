package com.entidadfinanciera.app.domain.exception;

public class TransferenciaEntreMismaCuentaException extends RuntimeException {
    public TransferenciaEntreMismaCuentaException(String mensaje) { super(mensaje); }
}