package bo.tecnoweb.emailsystem.model;

import java.sql.Date;

public class DevolucionProveedor {
    private int id;
    private Date fechaDevolucion;
    private String observacion;
    private Integer proveedorId;
    private Integer usuarioId;
    
    public DevolucionProveedor() {}
    
    public DevolucionProveedor(int id, Date fechaDevolucion, String observacion, 
                              Integer proveedorId, Integer usuarioId) {
        this.id = id;
        this.fechaDevolucion = fechaDevolucion;
        this.observacion = observacion;
        this.proveedorId = proveedorId;
        this.usuarioId = usuarioId;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Date getFechaDevolucion() {
        return fechaDevolucion;
    }
    
    public void setFechaDevolucion(Date fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }
    
    public String getObservacion() {
        return observacion;
    }
    
    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
    
    public Integer getProveedorId() {
        return proveedorId;
    }
    
    public void setProveedorId(Integer proveedorId) {
        this.proveedorId = proveedorId;
    }
    
    public Integer getUsuarioId() {
        return usuarioId;
    }
    
    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    @Override
    public String toString() {
        return "DevolucionProveedor{" +
                "id=" + id +
                ", fechaDevolucion=" + fechaDevolucion +
                ", observacion='" + observacion + '\'' +
                ", proveedorId=" + proveedorId +
                ", usuarioId=" + usuarioId +
                '}';
    }
}
