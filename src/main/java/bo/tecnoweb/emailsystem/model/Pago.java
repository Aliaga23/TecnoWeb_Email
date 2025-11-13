package bo.tecnoweb.emailsystem.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Pago {
    private int id;
    private BigDecimal monto;
    private String metodo; // 'qr', 'tarjeta', 'efectivo'
    private Date fechaPago;
    private int ventaId;
    
    public Pago() {}
    
    public Pago(int id, BigDecimal monto, String metodo, Date fechaPago, int ventaId) {
        this.id = id;
        this.monto = monto;
        this.metodo = metodo;
        this.fechaPago = fechaPago;
        this.ventaId = ventaId;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public BigDecimal getMonto() {
        return monto;
    }
    
    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
    
    public String getMetodo() {
        return metodo;
    }
    
    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }
    
    public Date getFechaPago() {
        return fechaPago;
    }
    
    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }
    
    public int getVentaId() {
        return ventaId;
    }
    
    public void setVentaId(int ventaId) {
        this.ventaId = ventaId;
    }
    
    @Override
    public String toString() {
        return "Pago{" +
                "id=" + id +
                ", monto=" + monto +
                ", metodo='" + metodo + '\'' +
                ", fechaPago=" + fechaPago +
                ", ventaId=" + ventaId +
                '}';
    }
}
