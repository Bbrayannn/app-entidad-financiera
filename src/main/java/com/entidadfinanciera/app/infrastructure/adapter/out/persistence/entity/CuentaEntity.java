package com.entidadfinanciera.app.infrastructure.adapter.out.persistence.entity;

import com.entidadfinanciera.app.domain.model.EstadoCuenta;
import com.entidadfinanciera.app.domain.model.TipoCuenta;
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
    private TipoCuenta tipoCuenta;

    @Column(name = "numero_cuenta", nullable = false, unique = true)
    private String numeroCuenta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCuenta estado;

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

    @Version
    @Column(nullable = false)
    private Long version;

    protected CuentaEntity() {}

    public CuentaEntity(Long id, TipoCuenta tipoCuenta, String numeroCuenta, EstadoCuenta estado,
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