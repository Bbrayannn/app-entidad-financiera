package com.entidadfinanciera.app.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad de dominio que representa una cuenta bancaria (Ahorros o Corriente).
 * Encapsula el saldo y la lógica de validación de estados y transiciones.
 */
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

    /**
     * Valida si la cuenta permite realizar retiros o transferencias según su estado actual.
     *
     * @return true si la cuenta está en estado ACTIVA
     */
    public boolean puedeCancelarse() {
        return saldo.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean esAhorros() {
        return tipoCuenta == TipoCuenta.AHORROS;
    }

    /**
     * Verifica si la cuenta puede cambiar al nuevo estado solicitado.
     *
     * @param nuevoEstado Estado al que se desea transicionar
     * @return true si la transición de estado es válida
     */

    public boolean puedeTransicionarA(EstadoCuenta nuevoEstado) {
        if (this.estado == EstadoCuenta.CANCELADA) {
            return false; // estado terminal, no admite ningún cambio
        }
        return this.estado != nuevoEstado; // no permitir "cambiar" al mismo estado
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