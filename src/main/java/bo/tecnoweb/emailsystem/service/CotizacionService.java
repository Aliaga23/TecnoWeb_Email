package bo.tecnoweb.emailsystem.service;

import bo.tecnoweb.emailsystem.config.Config;
import bo.tecnoweb.emailsystem.model.Cotizacion;
import bo.tecnoweb.emailsystem.model.DetalleCotizacion;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CotizacionService {
    
    public int insertar(Cotizacion cotizacion) throws SQLException {
        String sql = "INSERT INTO cotizacion (fecha_cotizacion, total, cliente_id) " +
                    "VALUES (?, ?, ?) RETURNING id";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, cotizacion.getFechaCotizacion());
            stmt.setBigDecimal(2, cotizacion.getTotal());
            
            if (cotizacion.getClienteId() != null) {
                stmt.setInt(3, cotizacion.getClienteId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("No se pudo obtener el ID de la cotizacion");
        }
    }
    
    public void insertarDetalle(DetalleCotizacion detalle) throws SQLException {
        String sql = "INSERT INTO detalle_cotizacion (cantidad, costo_unitario, subtotal, cotizacion_id, producto_id) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, detalle.getCantidad());
            stmt.setBigDecimal(2, detalle.getCostoUnitario());
            stmt.setBigDecimal(3, detalle.getSubtotal());
            stmt.setInt(4, detalle.getCotizacionId());
            stmt.setInt(5, detalle.getProductoId());
            
            stmt.executeUpdate();
        }
    }
    
    public Cotizacion buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, fecha_cotizacion, total, cliente_id " +
                    "FROM cotizacion WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Cotizacion(
                    rs.getInt("id"),
                    rs.getDate("fecha_cotizacion"),
                    rs.getBigDecimal("total"),
                    (Integer) rs.getObject("cliente_id")
                );
            }
            return null;
        }
    }
    
    public List<Cotizacion> listarTodas() throws SQLException {
        List<Cotizacion> cotizaciones = new ArrayList<>();
        String sql = "SELECT id, fecha_cotizacion, total, cliente_id " +
                    "FROM cotizacion ORDER BY fecha_cotizacion DESC";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                cotizaciones.add(new Cotizacion(
                    rs.getInt("id"),
                    rs.getDate("fecha_cotizacion"),
                    rs.getBigDecimal("total"),
                    (Integer) rs.getObject("cliente_id")
                ));
            }
        }
        return cotizaciones;
    }
    
    public List<Cotizacion> listarPorCliente(int clienteId) throws SQLException {
        List<Cotizacion> cotizaciones = new ArrayList<>();
        String sql = "SELECT id, fecha_cotizacion, total, cliente_id " +
                    "FROM cotizacion WHERE cliente_id = ? ORDER BY fecha_cotizacion DESC";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, clienteId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                cotizaciones.add(new Cotizacion(
                    rs.getInt("id"),
                    rs.getDate("fecha_cotizacion"),
                    rs.getBigDecimal("total"),
                    (Integer) rs.getObject("cliente_id")
                ));
            }
        }
        return cotizaciones;
    }
    
    public List<DetalleCotizacion> listarDetalles(int cotizacionId) throws SQLException {
        List<DetalleCotizacion> detalles = new ArrayList<>();
        String sql = "SELECT id, cantidad, costo_unitario, subtotal, cotizacion_id, producto_id " +
                    "FROM detalle_cotizacion WHERE cotizacion_id = ?";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, cotizacionId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                detalles.add(new DetalleCotizacion(
                    rs.getInt("id"),
                    rs.getInt("cantidad"),
                    rs.getBigDecimal("costo_unitario"),
                    rs.getBigDecimal("subtotal"),
                    rs.getInt("cotizacion_id"),
                    rs.getInt("producto_id")
                ));
            }
        }
        return detalles;
    }
    
    public void crearCotizacionCompleta(int clienteId, String productos) throws SQLException {
        ProductoService productoService = new ProductoService();
        
        // Crear la cotizacion
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setFechaCotizacion(new Date(System.currentTimeMillis()));
        cotizacion.setClienteId(clienteId);
        
        BigDecimal totalGeneral = BigDecimal.ZERO;
        List<DetalleCotizacion> detalles = new ArrayList<>();
        
        // Procesar los productos: "producto_id:cantidad,producto_id:cantidad"
        String[] productosArray = productos.split(",");
        
        for (String productoInfo : productosArray) {
            String[] partes = productoInfo.trim().split(":");
            if (partes.length != 2) {
                throw new SQLException("Formato invalido de producto: " + productoInfo + ". Use: id:cantidad");
            }
            
            int productoId = Integer.parseInt(partes[0].trim());
            int cantidad = Integer.parseInt(partes[1].trim());
            
            // Buscar el producto para obtener el precio
            var producto = productoService.buscarPorId(productoId);
            if (producto == null) {
                throw new SQLException("No existe el producto con ID: " + productoId);
            }
            
            BigDecimal precio = producto.getPrecioUnitario();
            BigDecimal subtotal = precio.multiply(new BigDecimal(cantidad));
            
            totalGeneral = totalGeneral.add(subtotal);
            
            DetalleCotizacion detalle = new DetalleCotizacion();
            detalle.setProductoId(productoId);
            detalle.setCantidad(cantidad);
            detalle.setCostoUnitario(precio);
            detalle.setSubtotal(subtotal);
            
            detalles.add(detalle);
        }
        
        cotizacion.setTotal(totalGeneral);
        int cotizacionId = insertar(cotizacion);
        
        // Insertar detalles
        for (DetalleCotizacion detalle : detalles) {
            detalle.setCotizacionId(cotizacionId);
            insertarDetalle(detalle);
        }
    }
}
