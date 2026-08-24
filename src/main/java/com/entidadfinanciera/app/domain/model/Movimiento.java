package com.entidadfinanciera.app.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Movimiento {

    private Long id;
    private Long transaccionId;
    private Long cuentaId;
    private TipoMovimiento tipoMovimiento;
    private BigDecimal monto;
    private BigDecimal saldoResultante;
    private LocalDateTime fechaCreacion;

    /**
     * Afectación individual a una cuenta dentro de una transacción (débito o crédito).
     * Guardo saldoResultante (el saldo justo después de aplicar este movimiento) para
     * poder reconstruir el estado de cuenta histórico sin tener que recalcular sumando
     * todos los movimientos anteriores cada vez que alguien lo consulta.
     */

    public Movimiento(Long id, Long transaccionId, Long cuentaId, TipoMovimiento tipoMovimiento,
                      BigDecimal monto, BigDecimal saldoResultante, LocalDateTime fechaCreacion) {
        this.id = id;
        this.transaccionId = transaccionId;
        this.cuentaId = cuentaId;
        this.tipoMovimiento = tipoMovimiento;
        this.monto = monto;
        this.saldoResultante = saldoResultante;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTransaccionId() { return transaccionId; }
    public void setTransaccionId(Long transaccionId) { this.transaccionId = transaccionId; }
    public Long getCuentaId() { return cuentaId; }
    public void setCuentaId(Long cuentaId) { this.cuentaId = cuentaId; }
    public TipoMovimiento getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public BigDecimal getSaldoResultante() { return saldoResultante; }
    public void setSaldoResultante(BigDecimal saldoResultante) { this.saldoResultante = saldoResultante; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}