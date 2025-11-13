package bo.tecnoweb.emailsystem.model;

public class DetalleDevolucionProveedor {
    private int id;
    private int idDevolucionProveedor;
    private int productoId;
    private int cantidad;
    
    public DetalleDevolucionProveedor() {}
    
    public DetalleDevolucionProveedor(int id, int idDevolucionProveedor, int productoId, int cantidad) {
        this.id = id;
        this.idDevolucionProveedor = idDevolucionProveedor;
        this.productoId = productoId;
        this.cantidad = cantidad;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getIdDevolucionProveedor() {
        return idDevolucionProveedor;
    }
    
    public void setIdDevolucionProveedor(int idDevolucionProveedor) {
        this.idDevolucionProveedor = idDevolucionProveedor;
    }
    
    public int getProductoId() {
        return productoId;
    }
    
    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }
    
    public int getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
    @Override
    public String toString() {
        return "DetalleDevolucionProveedor{" +
                "id=" + id +
                ", idDevolucionProveedor=" + idDevolucionProveedor +
                ", productoId=" + productoId +
                ", cantidad=" + cantidad +
                '}';
    }
}
