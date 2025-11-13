package bo.tecnoweb.emailsystem.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Cotizacion {
    private int id;
    private Date fechaCotizacion;
    private BigDecimal total;
    private Integer clienteId;
    
    public Cotizacion() {}
    
    public Cotizacion(int id, Date fechaCotizacion, BigDecimal total, Integer clienteId) {
        this.id = id;
        this.fechaCotizacion = fechaCotizacion;
        this.total = total;
        this.clienteId = clienteId;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Date getFechaCotizacion() {
        return fechaCotizacion;
    }
    
    public void setFechaCotizacion(Date fechaCotizacion) {
        this.fechaCotizacion = fechaCotizacion;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public Integer getClienteId() {
        return clienteId;
    }
    
    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }
    
    @Override
    public String toString() {
        return "Cotizacion{" +
                "id=" + id +
                ", fechaCotizacion=" + fechaCotizacion +
                ", total=" + total +
                ", clienteId=" + clienteId +
                '}';
    }
}
