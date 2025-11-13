package bo.tecnoweb.emailsystem.service;

import bo.tecnoweb.emailsystem.config.Config;
import bo.tecnoweb.emailsystem.model.Rol;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RolService {
    
    public Rol buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre FROM rol WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Rol(
                    rs.getInt("id"),
                    rs.getString("nombre")
                );
            }
            return null;
        }
    }
    
    public Rol buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT id, nombre FROM rol WHERE LOWER(nombre) = LOWER(?)";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Rol(
                    rs.getInt("id"),
                    rs.getString("nombre")
                );
            }
            return null;
        }
    }
    
    public List<Rol> listarTodos() throws SQLException {
        List<Rol> roles = new ArrayList<>();
        String sql = "SELECT id, nombre FROM rol ORDER BY id";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                roles.add(new Rol(
                    rs.getInt("id"),
                    rs.getString("nombre")
                ));
            }
        }
        return roles;
    }
    
    public boolean insertar(String nombre) throws SQLException {
        String sql = "INSERT INTO rol (nombre) VALUES (?)";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nombre);
            return stmt.executeUpdate() > 0;
        }
    }
}
