package bo.tecnoweb.emailsystem.service;

import bo.tecnoweb.emailsystem.config.Config;
import bo.tecnoweb.emailsystem.model.Pago;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PagoService {
    
    public List<Pago> listarPorCliente(int clienteId) throws SQLException {
        List<Pago> pagos = new ArrayList<>();
        String sql = "SELECT p.id, p.fecha_pago, p.monto, p.metodo, p.venta_id " +
                    "FROM pago p " +
                    "INNER JOIN venta v ON p.venta_id = v.id " +
                    "WHERE v.cliente_id = ? ORDER BY p.fecha_pago DESC";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, clienteId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                pagos.add(new Pago(
                    rs.getInt("id"),
                    rs.getBigDecimal("monto"),
                    rs.getString("metodo"),
                    rs.getDate("fecha_pago"),
                    rs.getInt("venta_id")
                ));
            }
        }
        return pagos;
    }
    
    public List<Pago> listarPorVenta(int ventaId) throws SQLException {
        List<Pago> pagos = new ArrayList<>();
        String sql = "SELECT id, fecha_pago, monto, metodo, venta_id " +
                    "FROM pago WHERE venta_id = ? ORDER BY fecha_pago DESC";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, ventaId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                pagos.add(new Pago(
                    rs.getInt("id"),
                    rs.getBigDecimal("monto"),
                    rs.getString("metodo"),
                    rs.getDate("fecha_pago"),
                    rs.getInt("venta_id")
                ));
            }
        }
        return pagos;
    }
}
