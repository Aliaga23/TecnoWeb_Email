package bo.tecnoweb.emailsystem.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Producto {
    private int id;
    private String nombre;
    private String descripcion;
    private int stockActual;
    private BigDecimal precioUnitario;
    private Timestamp creadoEn;
    private Integer categoriaId;
    
    public Producto() {}
    
    public Producto(int id, String nombre, String descripcion, int stockActual, 
                   BigDecimal precioUnitario, Timestamp creadoEn, Integer categoriaId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.stockActual = stockActual;
        this.precioUnitario = precioUnitario;
        this.creadoEn = creadoEn;
        this.categoriaId = categoriaId;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public int getStockActual() {
        return stockActual;
    }
    
    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }
    
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    
    public Timestamp getCreadoEn() {
        return creadoEn;
    }
    
    public void setCreadoEn(Timestamp creadoEn) {
        this.creadoEn = creadoEn;
    }
    
    public Integer getCategoriaId() {
        return categoriaId;
    }
    
    public void setCategoriaId(Integer categoriaId) {
        this.categoriaId = categoriaId;
    }
    
    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", stockActual=" + stockActual +
                ", precioUnitario=" + precioUnitario +
                ", creadoEn=" + creadoEn +
                ", categoriaId=" + categoriaId +
                '}';
    }
}
