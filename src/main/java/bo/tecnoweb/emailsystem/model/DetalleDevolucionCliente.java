package bo.tecnoweb.emailsystem.model;

public class DetalleDevolucionCliente {
    private int id;
    private int cantidad;
    private int devolucionId;
    private int productoId;
    
    public DetalleDevolucionCliente() {}
    
    public DetalleDevolucionCliente(int id, int cantidad, int devolucionId, int productoId) {
        this.id = id;
        this.cantidad = cantidad;
        this.devolucionId = devolucionId;
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
    
    public int getDevolucionId() {
        return devolucionId;
    }
    
    public void setDevolucionId(int devolucionId) {
        this.devolucionId = devolucionId;
    }
    
    public int getProductoId() {
        return productoId;
    }
    
    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }
    
    @Override
    public String toString() {
        return "DetalleDevolucionCliente{" +
                "id=" + id +
                ", cantidad=" + cantidad +
                ", devolucionId=" + devolucionId +
                ", productoId=" + productoId +
                '}';
    }
}
