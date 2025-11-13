package bo.tecnoweb.emailsystem.service;

import bo.tecnoweb.emailsystem.config.Config;
import bo.tecnoweb.emailsystem.model.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoService {
    
    public Producto buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, descripcion, stock_actual, precio_unitario, creado_en, categoria_id " +
                    "FROM producto WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Producto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getInt("stock_actual"),
                    rs.getBigDecimal("precio_unitario"),
                    rs.getTimestamp("creado_en"),
                    (Integer) rs.getObject("categoria_id")
                );
            }
            return null;
        }
    }
    
    public Producto buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT id, nombre, descripcion, stock_actual, precio_unitario, creado_en, categoria_id " +
                    "FROM producto WHERE LOWER(nombre) = LOWER(?)";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Producto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getInt("stock_actual"),
                    rs.getBigDecimal("precio_unitario"),
                    rs.getTimestamp("creado_en"),
                    (Integer) rs.getObject("categoria_id")
                );
            }
            return null;
        }
    }
    
    public List<Producto> listarTodos() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion, stock_actual, precio_unitario, creado_en, categoria_id " +
                    "FROM producto ORDER BY nombre";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                productos.add(new Producto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getInt("stock_actual"),
                    rs.getBigDecimal("precio_unitario"),
                    rs.getTimestamp("creado_en"),
                    (Integer) rs.getObject("categoria_id")
                ));
            }
        }
        return productos;
    }
    
    public List<Producto> listarPorCategoria(int categoriaId) throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion, stock_actual, precio_unitario, creado_en, categoria_id " +
                    "FROM producto WHERE categoria_id = ? ORDER BY nombre";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, categoriaId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                productos.add(new Producto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getInt("stock_actual"),
                    rs.getBigDecimal("precio_unitario"),
                    rs.getTimestamp("creado_en"),
                    (Integer) rs.getObject("categoria_id")
                ));
            }
        }
        return productos;
    }
    
    public boolean insertar(Producto producto) throws SQLException {
        String sql = "INSERT INTO producto (nombre, descripcion, stock_actual, precio_unitario, categoria_id) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            stmt.setInt(3, producto.getStockActual());
            stmt.setBigDecimal(4, producto.getPrecioUnitario());
            stmt.setObject(5, producto.getCategoriaId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean actualizar(Producto producto) throws SQLException {
        String sql = "UPDATE producto SET nombre = ?, descripcion = ?, stock_actual = ?, " +
                    "precio_unitario = ?, categoria_id = ? WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            stmt.setInt(3, producto.getStockActual());
            stmt.setBigDecimal(4, producto.getPrecioUnitario());
            stmt.setObject(5, producto.getCategoriaId());
            stmt.setInt(6, producto.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean actualizarStock(int productoId, int nuevoStock) throws SQLException {
        String sql = "UPDATE producto SET stock_actual = ? WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, nuevoStock);
            stmt.setInt(2, productoId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM producto WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public List<Producto> listarPorNombreCategoria(String nombreCategoria) throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.id, p.nombre, p.descripcion, p.stock_actual, p.precio_unitario, p.creado_en, p.categoria_id " +
                    "FROM producto p " +
                    "INNER JOIN categoria c ON p.categoria_id = c.id " +
                    "WHERE LOWER(c.nombre) = LOWER(?) ORDER BY p.nombre";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nombreCategoria);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                productos.add(new Producto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getInt("stock_actual"),
                    rs.getBigDecimal("precio_unitario"),
                    rs.getTimestamp("creado_en"),
                    (Integer) rs.getObject("categoria_id")
                ));
            }
        }
        return productos;
    }
}
