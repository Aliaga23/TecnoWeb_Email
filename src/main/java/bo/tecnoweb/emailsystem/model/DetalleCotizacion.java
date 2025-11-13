package bo.tecnoweb.emailsystem.model;

import java.math.BigDecimal;

public class DetalleCotizacion {
    private int id;
    private int cantidad;
    private BigDecimal costoUnitario;
    private BigDecimal subtotal;
    private int cotizacionId;
    private int productoId;
    
    public DetalleCotizacion() {}
    
    public DetalleCotizacion(int id, int cantidad, BigDecimal costoUnitario, BigDecimal subtotal, 
                            int cotizacionId, int productoId) {
        this.id = id;
        this.cantidad = cantidad;
        this.costoUnitario = costoUnitario;
        this.subtotal = subtotal;
        this.cotizacionId = cotizacionId;
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
    
    public BigDecimal getCostoUnitario() {
        return costoUnitario;
    }
    
    public void setCostoUnitario(BigDecimal costoUnitario) {
        this.costoUnitario = costoUnitario;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public int getCotizacionId() {
        return cotizacionId;
    }
    
    public void setCotizacionId(int cotizacionId) {
        this.cotizacionId = cotizacionId;
    }
    
    public int getProductoId() {
        return productoId;
    }
    
    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }
    
    @Override
    public String toString() {
        return "DetalleCotizacion{" +
                "id=" + id +
                ", cantidad=" + cantidad +
                ", costoUnitario=" + costoUnitario +
                ", subtotal=" + subtotal +
                ", cotizacionId=" + cotizacionId +
                ", productoId=" + productoId +
                '}';
    }
}
