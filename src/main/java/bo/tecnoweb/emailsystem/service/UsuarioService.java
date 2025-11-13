package bo.tecnoweb.emailsystem.service;

import bo.tecnoweb.emailsystem.config.Config;
import bo.tecnoweb.emailsystem.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioService {
    
    public Usuario buscarPorCorreo(String correo) throws SQLException {
        String sql = "SELECT id, nombre, apellido, ci, telefono, correo, rol_id " +
                    "FROM usuario WHERE LOWER(correo) = LOWER(?)";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, correo);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Usuario(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("ci"),
                    rs.getString("telefono"),
                    rs.getString("correo"),
                    rs.getInt("rol_id")
                );
            }
            return null;
        }
    }
    
    public Usuario buscarPorCI(String ci) throws SQLException {
        String sql = "SELECT id, nombre, apellido, ci, telefono, correo, rol_id " +
                    "FROM usuario WHERE ci = ?";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ci);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Usuario(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("ci"),
                    rs.getString("telefono"),
                    rs.getString("correo"),
                    rs.getInt("rol_id")
                );
            }
            return null;
        }
    }
    
    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT u.id, u.nombre, u.apellido, u.ci, u.telefono, u.correo, u.rol_id " +
                    "FROM usuario u ORDER BY u.id";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                usuarios.add(new Usuario(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("ci"),
                    rs.getString("telefono"),
                    rs.getString("correo"),
                    rs.getInt("rol_id")
                ));
            }
        }
        return usuarios;
    }
    
    public List<Usuario> listarPorRol(int rolId) throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, nombre, apellido, ci, telefono, correo, rol_id " +
                    "FROM usuario WHERE rol_id = ? ORDER BY id";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, rolId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                usuarios.add(new Usuario(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("ci"),
                    rs.getString("telefono"),
                    rs.getString("correo"),
                    rs.getInt("rol_id")
                ));
            }
        }
        return usuarios;
    }
    
    public boolean insertar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (ci, nombre, apellido, telefono, correo, rol_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getCi());
            stmt.setString(2, usuario.getNombre());
            stmt.setString(3, usuario.getApellido());
            stmt.setString(4, usuario.getTelefono());
            stmt.setString(5, usuario.getCorreo());
            stmt.setInt(6, usuario.getRolId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean actualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuario SET nombre = ?, apellido = ?, telefono = ?, " +
                    "correo = ?, rol_id = ? WHERE ci = ?";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getApellido());
            stmt.setString(3, usuario.getTelefono());
            stmt.setString(4, usuario.getCorreo());
            stmt.setInt(5, usuario.getRolId());
            stmt.setString(6, usuario.getCi());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean eliminar(String ci) throws SQLException {
        String sql = "DELETE FROM usuario WHERE ci = ?";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ci);
            return stmt.executeUpdate() > 0;
        }
    }
}
