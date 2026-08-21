package com.entidadfinanciera.app.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Cuenta {

    private Long id;
    private TipoCuenta tipoCuenta;
    private String numeroCuenta;
    private EstadoCuenta estado;
    private BigDecimal saldo;
    private boolean exentaGmf;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private Long clienteId;

    public Cuenta(Long id, TipoCuenta tipoCuenta, String numeroCuenta, EstadoCuenta estado,
                  BigDecimal saldo, boolean exentaGmf, LocalDateTime fechaCreacion,
                  LocalDateTime fechaModificacion, Long clienteId) {
        this.id = id;
        this.tipoCuenta = tipoCuenta;
        this.numeroCuenta = numeroCuenta;
        this.estado = estado;
        this.saldo = saldo;
        this.exentaGmf = exentaGmf;
        this.fechaCreacion = fechaCreacion;
        this.fechaModificacion = fechaModificacion;
        this.clienteId = clienteId;
    }

    public boolean puedeCancelarse() {
        return saldo.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean esAhorros() {
        return tipoCuenta == TipoCuenta.AHORROS;
    }

    public void validarSaldoParaTipo(BigDecimal nuevoSaldo) {
        if (esAhorros() && nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Una cuenta de ahorros no puede tener saldo negativo.");
        }
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TipoCuenta getTipoCuenta() { return tipoCuenta; }
    public void setTipoCuenta(TipoCuenta tipoCuenta) { this.tipoCuenta = tipoCuenta; }
    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }
    public EstadoCuenta getEstado() { return estado; }
    public void setEstado(EstadoCuenta estado) { this.estado = estado; }
    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
    public boolean isExentaGmf() { return exentaGmf; }
    public void setExentaGmf(boolean exentaGmf) { this.exentaGmf = exentaGmf; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
}