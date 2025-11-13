package bo.tecnoweb.emailsystem.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Venta {
    private int id;
    private Date fechaVenta;
    private String tipo; // 'contado' o 'credito'
    private BigDecimal total;
    private String estado; // 'pendiente' o 'pagada'
    private Integer clienteId;
    private Integer vendedorId;
    private Integer cotizacionId;
    
    public Venta() {}
    
    public Venta(int id, Date fechaVenta, String tipo, BigDecimal total, String estado,
                Integer clienteId, Integer vendedorId, Integer cotizacionId) {
        this.id = id;
        this.fechaVenta = fechaVenta;
        this.tipo = tipo;
        this.total = total;
        this.estado = estado;
        this.clienteId = clienteId;
        this.vendedorId = vendedorId;
        this.cotizacionId = cotizacionId;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Date getFechaVenta() {
        return fechaVenta;
    }
    
    public void setFechaVenta(Date fechaVenta) {
        this.fechaVenta = fechaVenta;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public Integer getClienteId() {
        return clienteId;
    }
    
    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }
    
    public Integer getVendedorId() {
        return vendedorId;
    }
    
    public void setVendedorId(Integer vendedorId) {
        this.vendedorId = vendedorId;
    }
    
    public Integer getCotizacionId() {
        return cotizacionId;
    }
    
    public void setCotizacionId(Integer cotizacionId) {
        this.cotizacionId = cotizacionId;
    }
    
    @Override
    public String toString() {
        return "Venta{" +
                "id=" + id +
                ", fechaVenta=" + fechaVenta +
                ", tipo='" + tipo + '\'' +
                ", total=" + total +
                ", estado='" + estado + '\'' +
                ", clienteId=" + clienteId +
                ", vendedorId=" + vendedorId +
                ", cotizacionId=" + cotizacionId +
                '}';
    }
}
