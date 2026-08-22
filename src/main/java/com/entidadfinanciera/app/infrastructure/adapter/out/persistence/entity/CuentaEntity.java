package com.entidadfinanciera.app.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cuentas")
public class CuentaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_cuenta", nullable = false)
    @Enumerated(EnumType.STRING)
    private com.entidadfinanciera.app.domain.model.TipoCuenta tipoCuenta;

    @Column(name = "numero_cuenta", nullable = false, length = 10, columnDefinition = "bpchar")
    private String numeroCuenta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private com.entidadfinanciera.app.domain.model.EstadoCuenta estado;

    @Column(nullable = false)
    private BigDecimal saldo;

    @Column(name = "exenta_gmf", nullable = false)
    private boolean exentaGmf;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    protected CuentaEntity() {}

    public CuentaEntity(Long id, com.entidadfinanciera.app.domain.model.TipoCuenta tipoCuenta, String numeroCuenta,
                        com.entidadfinanciera.app.domain.model.EstadoCuenta estado, BigDecimal saldo,
                        boolean exentaGmf, LocalDateTime fechaCreacion, LocalDateTime fechaModificacion,
                        Long clienteId) {
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public com.entidadfinanciera.app.domain.model.TipoCuenta getTipoCuenta() { return tipoCuenta; }
    public void setTipoCuenta(com.entidadfinanciera.app.domain.model.TipoCuenta tipoCuenta) { this.tipoCuenta = tipoCuenta; }
    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }
    public com.entidadfinanciera.app.domain.model.EstadoCuenta getEstado() { return estado; }
    public void setEstado(com.entidadfinanciera.app.domain.model.EstadoCuenta estado) { this.estado = estado; }
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