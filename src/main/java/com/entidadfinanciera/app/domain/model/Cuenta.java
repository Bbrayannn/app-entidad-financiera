package com.entidadfinanciera.app.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Cuenta financiera (corriente o ahorros) vinculada a un cliente.
 * Uso BigDecimal para el saldo en lugar de double: el punto flotante binario
 * en el dinero no se puede redondear
 * El campo version soporta bloqueo optimista (JPA @Version en la entidad):
 * evita que dos transacciones simultáneas sobre la misma cuenta se pisen el saldo y se modifiquen
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
    private Long version;

    public Cuenta(Long id, TipoCuenta tipoCuenta, String numeroCuenta, EstadoCuenta estado,
                  BigDecimal saldo, boolean exentaGmf, LocalDateTime fechaCreacion,
                  LocalDateTime fechaModificacion, Long clienteId, Long version) {
        this.id = id;
        this.tipoCuenta = tipoCuenta;
        this.numeroCuenta = numeroCuenta;
        this.estado = estado;
        this.saldo = saldo;
        this.exentaGmf = exentaGmf;
        this.fechaCreacion = fechaCreacion;
        this.fechaModificacion = fechaModificacion;
        this.clienteId = clienteId;
        this.version = version;
    }

    public boolean puedeCancelarse() {
        return saldo.compareTo(BigDecimal.ZERO) == 0;
    }
    /** El PDF exige que solo se pueda cancelar una cuenta cuando su saldo está exactamente en $0. */

    public boolean esAhorros() {
        return tipoCuenta == TipoCuenta.AHORROS;
    }

    /**
     * Ahorros nunca puede quedar en negativo; corriente sí puede (permite sobregiro),
     * porque el PDF solo restringe el saldo negativo para el tipo ahorros explícitamente.
     */

    public void validarSaldoParaTipo(BigDecimal nuevoSaldo) {
        if (esAhorros() && nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Una cuenta de ahorros no puede tener saldo negativo.");
        }
    }


    /**
     * Define qué cambios de estado son válidos. CANCELADA es terminal (una vez cancelada,
     * la cuenta no vuelve a operar) y no se permite "cambiar" a un estado igual al actual,
     * porque eso no representa ninguna transición real por lo mismo que ya esta cancelada
     */

    public boolean puedeTransicionarA(EstadoCuenta nuevoEstado) {
        if (this.estado == EstadoCuenta.CANCELADA) {
            return false;
        }
        return this.estado != nuevoEstado;
    }

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
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}