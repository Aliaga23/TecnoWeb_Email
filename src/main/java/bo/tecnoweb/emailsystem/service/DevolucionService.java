package bo.tecnoweb.emailsystem.service;

import bo.tecnoweb.emailsystem.config.Config;
import bo.tecnoweb.emailsystem.model.Devolucion;
import bo.tecnoweb.emailsystem.model.DetalleDevolucionCliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DevolucionService {
    
    private ProductoService productoService;
    
    public DevolucionService() {
        this.productoService = new ProductoService();
    }
    
    public int insertar(Devolucion devolucion) throws SQLException {
        String sql = "INSERT INTO devolucion (fecha_devolucion, motivo, venta_id) " +
                    "VALUES (?, ?, ?) RETURNING id";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, devolucion.getFechaDevolucion());
            stmt.setString(2, devolucion.getMotivo());
            
            if (devolucion.getVentaId() != null) {
                stmt.setInt(3, devolucion.getVentaId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("No se pudo obtener el ID de la devolucion");
        }
    }
    
    public void insertarDetalle(DetalleDevolucionCliente detalle) throws SQLException {
        String sql = "INSERT INTO detalle_devolucion_cliente (cantidad, devolucion_id, producto_id) " +
                    "VALUES (?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, detalle.getCantidad());
            stmt.setInt(2, detalle.getDevolucionId());
            stmt.setInt(3, detalle.getProductoId());
            
            stmt.executeUpdate();
        }
    }
    
    public List<Devolucion> listarTodas() throws SQLException {
        List<Devolucion> devoluciones = new ArrayList<>();
        String sql = "SELECT id, fecha_devolucion, motivo, venta_id " +
                    "FROM devolucion ORDER BY fecha_devolucion DESC";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                devoluciones.add(new Devolucion(
                    rs.getInt("id"),
                    rs.getDate("fecha_devolucion"),
                    rs.getString("motivo"),
                    (Integer) rs.getObject("venta_id")
                ));
            }
        }
        return devoluciones;
    }
    
    public Devolucion buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, fecha_devolucion, motivo, venta_id " +
                    "FROM devolucion WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Devolucion(
                    rs.getInt("id"),
                    rs.getDate("fecha_devolucion"),
                    rs.getString("motivo"),
                    (Integer) rs.getObject("venta_id")
                );
            }
            return null;
        }
    }
    
    public List<Devolucion> listarPorVenta(int ventaId) throws SQLException {
        List<Devolucion> devoluciones = new ArrayList<>();
        String sql = "SELECT id, fecha_devolucion, motivo, venta_id " +
                    "FROM devolucion WHERE venta_id = ? ORDER BY fecha_devolucion DESC";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, ventaId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                devoluciones.add(new Devolucion(
                    rs.getInt("id"),
                    rs.getDate("fecha_devolucion"),
                    rs.getString("motivo"),
                    (Integer) rs.getObject("venta_id")
                ));
            }
        }
        return devoluciones;
    }
    
    public List<DetalleDevolucionCliente> listarDetalles(int devolucionId) throws SQLException {
        List<DetalleDevolucionCliente> detalles = new ArrayList<>();
        String sql = "SELECT id, cantidad, devolucion_id, producto_id " +
                    "FROM detalle_devolucion_cliente WHERE devolucion_id = ?";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, devolucionId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                detalles.add(new DetalleDevolucionCliente(
                    rs.getInt("id"),
                    rs.getInt("cantidad"),
                    rs.getInt("devolucion_id"),
                    rs.getInt("producto_id")
                ));
            }
        }
        return detalles;
    }
    
    public void registrarDevolucionCompleta(int ventaId, String productos, String motivo) throws SQLException {
        // Crear la devolucion
        Devolucion devolucion = new Devolucion();
        devolucion.setFechaDevolucion(new Date(System.currentTimeMillis()));
        devolucion.setMotivo(motivo);
        devolucion.setVentaId(ventaId);
        
        int devolucionId = insertar(devolucion);
        
        // Procesar los productos: "producto_id:cantidad,producto_id:cantidad"
        String[] productosArray = productos.split(",");
        
        for (String productoInfo : productosArray) {
            String[] partes = productoInfo.trim().split(":");
            if (partes.length != 2) {
                throw new SQLException("Formato invalido de producto: " + productoInfo);
            }
            
            int productoId = Integer.parseInt(partes[0].trim());
            int cantidad = Integer.parseInt(partes[1].trim());
            
            // Insertar detalle
            DetalleDevolucionCliente detalle = new DetalleDevolucionCliente();
            detalle.setDevolucionId(devolucionId);
            detalle.setProductoId(productoId);
            detalle.setCantidad(cantidad);
            insertarDetalle(detalle);
            
            // Aumentar stock del producto devuelto
            var producto = productoService.buscarPorId(productoId);
            if (producto != null) {
                int nuevoStock = producto.getStockActual() + cantidad;
                productoService.actualizarStock(productoId, nuevoStock);
            }
        }
    }
    
    public List<Devolucion> listarPorCliente(int clienteId) throws SQLException {
        List<Devolucion> devoluciones = new ArrayList<>();
        String sql = "SELECT d.id, d.fecha_devolucion, d.motivo, d.venta_id " +
                    "FROM devolucion d " +
                    "INNER JOIN venta v ON d.venta_id = v.id " +
                    "WHERE v.cliente_id = ? ORDER BY d.fecha_devolucion DESC";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, clienteId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                devoluciones.add(new Devolucion(
                    rs.getInt("id"),
                    rs.getDate("fecha_devolucion"),
                    rs.getString("motivo"),
                    rs.getInt("venta_id")
                ));
            }
        }
        return devoluciones;
    }
}
