package com.entidadfinanciera.app.domain.exception;

public class ClienteConProductosVinculadosException extends RuntimeException {
    public ClienteConProductosVinculadosException(String mensaje) {
        super(mensaje);
    }
}