package bo.tecnoweb.emailsystem.model;

import java.math.BigDecimal;

public class DetalleVenta {
    private int id;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private int ventaId;
    private int productoId;
    
    public DetalleVenta() {}
    
    public DetalleVenta(int id, int cantidad, BigDecimal precioUnitario, BigDecimal subtotal,
                       int ventaId, int productoId) {
        this.id = id;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
        this.ventaId = ventaId;
        this.productoId = productoId;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public int getVentaId() {
        return ventaId;
    }
    
    public void setVentaId(int ventaId) {
        this.ventaId = ventaId;
    }
    
    public int getProductoId() {
        return productoId;
    }
    
    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }
    
    @Override
    public String toString() {
        return "DetalleVenta{" +
                "id=" + id +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + subtotal +
                ", ventaId=" + ventaId +
                ", productoId=" + productoId +
                '}';
    }
}
