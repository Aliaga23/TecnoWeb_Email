package bo.tecnoweb.emailsystem.service;

import bo.tecnoweb.emailsystem.config.Config;
import bo.tecnoweb.emailsystem.model.Proveedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorService {
    
    public Proveedor buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, telefono, correo, direccion FROM proveedor WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Proveedor(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("telefono"),
                    rs.getString("correo"),
                    rs.getString("direccion")
                );
            }
            return null;
        }
    }
    
    public Proveedor buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT id, nombre, telefono, correo, direccion FROM proveedor " +
                    "WHERE LOWER(nombre) = LOWER(?)";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Proveedor(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("telefono"),
                    rs.getString("correo"),
                    rs.getString("direccion")
                );
            }
            return null;
        }
    }
    
    public List<Proveedor> listarTodos() throws SQLException {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT id, nombre, telefono, correo, direccion FROM proveedor ORDER BY nombre";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                proveedores.add(new Proveedor(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("telefono"),
                    rs.getString("correo"),
                    rs.getString("direccion")
                ));
            }
        }
        return proveedores;
    }
    
    public boolean insertar(Proveedor proveedor) throws SQLException {
        String sql = "INSERT INTO proveedor (nombre, telefono, correo, direccion) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, proveedor.getNombre());
            stmt.setString(2, proveedor.getTelefono());
            stmt.setString(3, proveedor.getCorreo());
            stmt.setString(4, proveedor.getDireccion());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean actualizar(Proveedor proveedor) throws SQLException {
        String sql = "UPDATE proveedor SET nombre = ?, telefono = ?, correo = ?, direccion = ? WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, proveedor.getNombre());
            stmt.setString(2, proveedor.getTelefono());
            stmt.setString(3, proveedor.getCorreo());
            stmt.setString(4, proveedor.getDireccion());
            stmt.setInt(5, proveedor.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM proveedor WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}
