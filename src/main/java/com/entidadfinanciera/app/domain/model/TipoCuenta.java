package com.entidadfinanciera.app.domain.model;

public enum TipoCuenta {
    CORRIENTE("33"),
    AHORROS("53");

    private final String prefijo;

    TipoCuenta(String prefijo) {
        this.prefijo = prefijo;
    }

    public String getPrefijo() {
        return prefijo;
    }
}