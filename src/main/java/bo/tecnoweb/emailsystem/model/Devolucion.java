package bo.tecnoweb.emailsystem.model;

import java.sql.Date;

public class Devolucion {
    private int id;
    private Date fechaDevolucion;
    private String motivo;
    private Integer ventaId;
    
    public Devolucion() {}
    
    public Devolucion(int id, Date fechaDevolucion, String motivo, Integer ventaId) {
        this.id = id;
        this.fechaDevolucion = fechaDevolucion;
        this.motivo = motivo;
        this.ventaId = ventaId;
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
    
    public String getMotivo() {
        return motivo;
    }
    
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
    
    public Integer getVentaId() {
        return ventaId;
    }
    
    public void setVentaId(Integer ventaId) {
        this.ventaId = ventaId;
    }
    
    @Override
    public String toString() {
        return "Devolucion{" +
                "id=" + id +
                ", fechaDevolucion=" + fechaDevolucion +
                ", motivo='" + motivo + '\'' +
                ", ventaId=" + ventaId +
                '}';
    }
}
