package bo.tecnoweb.emailsystem.database;

import bo.tecnoweb.emailsystem.config.Config;
import bo.tecnoweb.emailsystem.model.Persona;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            Config.getDbUrl(),
            Config.getDbUser(),
            Config.getDbPassword()
        );
    }
    
    public List<Persona> listarPersonas(String filtro) throws SQLException {
        List<Persona> personas = new ArrayList<>();
        String sql;
        
        if (filtro == null || filtro.equals("*") || filtro.trim().isEmpty()) {
            sql = "SELECT * FROM persona ORDER BY per_appm, per_nom";
        } else {
            sql = "SELECT * FROM persona WHERE " +
                  "LOWER(per_cod) LIKE ? OR " +
                  "LOWER(per_nom) LIKE ? OR " +
                  "LOWER(per_appm) LIKE ? OR " +
                  "LOWER(per_prof) LIKE ? OR " +
                  "LOWER(per_email) LIKE ? " +
                  "ORDER BY per_appm, per_nom";
        }
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (filtro != null && !filtro.equals("*") && !filtro.trim().isEmpty()) {
                String busqueda = "%" + filtro.toLowerCase() + "%";
                for (int i = 1; i <= 5; i++) {
                    pstmt.setString(i, busqueda);
                }
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Persona persona = new Persona();
                persona.setCi(rs.getString("per_cod"));
                persona.setNombres(rs.getString("per_nom"));
                persona.setApellidos(rs.getString("per_appm"));
                persona.setTipo(rs.getString("per_prof"));
                persona.setTelefono(rs.getString("per_telf"));
                persona.setCelular(rs.getString("per_cel"));
                persona.setEmail(rs.getString("per_email"));
                personas.add(persona);
            }
        }
        
        return personas;
    }
    
    public void insertarPersona(Persona persona) throws SQLException {
        String sql = "INSERT INTO persona (per_cod, per_nom, per_appm, per_prof, per_telf, per_cel, per_email) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, persona.getCi());
            pstmt.setString(2, persona.getNombres());
            pstmt.setString(3, persona.getApellidos());
            pstmt.setString(4, persona.getTipo());
            pstmt.setString(5, persona.getTelefono());
            pstmt.setString(6, persona.getCelular());
            pstmt.setString(7, persona.getEmail());
            
            pstmt.executeUpdate();
        }
    }
    
    public void modificarPersona(String ci, Persona persona) throws SQLException {
        String sql = "UPDATE persona SET " +
                    "per_nom = ?, per_appm = ?, per_prof = ?, " +
                    "per_telf = ?, per_cel = ?, per_email = ? " +
                    "WHERE per_cod = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, persona.getNombres());
            pstmt.setString(2, persona.getApellidos());
            pstmt.setString(3, persona.getTipo());
            pstmt.setString(4, persona.getTelefono());
            pstmt.setString(5, persona.getCelular());
            pstmt.setString(6, persona.getEmail());
            pstmt.setString(7, ci);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se encontró persona con CI: " + ci);
            }
        }
    }
    
    public void eliminarPersona(String ci) throws SQLException {
        String sql = "DELETE FROM persona WHERE per_cod = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ci);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No se encontró persona con CI: " + ci);
            }
        }
    }
    
    public boolean existePersona(String ci) throws SQLException {
        String sql = "SELECT COUNT(*) FROM persona WHERE per_cod = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ci);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        
        return false;
    }
    
    public void testConexion() {
        try (Connection conn = getConnection()) {
            System.out.println("✓ Conexión a base de datos exitosa");
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("  Database: " + meta.getDatabaseProductName());
            System.out.println("  Version: " + meta.getDatabaseProductVersion());
        } catch (SQLException e) {
            System.err.println("✗ Error de conexión a base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
