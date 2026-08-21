package com.entidadfinanciera.app.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Transaccion {

    private Long id;
    private TipoTransaccion tipoTransaccion;
    private BigDecimal monto;
    private String descripcion;
    private LocalDateTime fechaCreacion;
    private List<Movimiento> movimientos;

    public Transaccion(Long id, TipoTransaccion tipoTransaccion, BigDecimal monto,
                       String descripcion, LocalDateTime fechaCreacion, List<Movimiento> movimientos) {
        this.id = id;
        this.tipoTransaccion = tipoTransaccion;
        this.monto = monto;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
        this.movimientos = movimientos;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TipoTransaccion getTipoTransaccion() { return tipoTransaccion; }
    public void setTipoTransaccion(TipoTransaccion tipoTransaccion) { this.tipoTransaccion = tipoTransaccion; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public List<Movimiento> getMovimientos() { return movimientos; }
    public void setMovimientos(List<Movimiento> movimientos) { this.movimientos = movimientos; }
}