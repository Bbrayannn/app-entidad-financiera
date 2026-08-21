package com.entidadfinanciera.app.domain.exception;

public class TransferenciaMismaCuentaException extends RuntimeException {
    public TransferenciaMismaCuentaException(String mensaje) {
        super(mensaje);
    }
}