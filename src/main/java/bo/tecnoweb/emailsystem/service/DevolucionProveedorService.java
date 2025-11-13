package bo.tecnoweb.emailsystem.service;

import bo.tecnoweb.emailsystem.config.Config;
import bo.tecnoweb.emailsystem.model.DevolucionProveedor;
import bo.tecnoweb.emailsystem.model.DetalleDevolucionProveedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DevolucionProveedorService {
    
    private ProductoService productoService;
    
    public DevolucionProveedorService() {
        this.productoService = new ProductoService();
    }
    
    public int insertar(DevolucionProveedor devolucion) throws SQLException {
        String sql = "INSERT INTO devolucion_proveedor (fecha_devolucion, observacion, proveedor_id, usuario_id) " +
                    "VALUES (?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, devolucion.getFechaDevolucion());
            stmt.setString(2, devolucion.getObservacion());
            
            if (devolucion.getProveedorId() != null) {
                stmt.setInt(3, devolucion.getProveedorId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            
            if (devolucion.getUsuarioId() != null) {
                stmt.setInt(4, devolucion.getUsuarioId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("No se pudo obtener el ID de la devolucion");
        }
    }
    
    public void insertarDetalle(DetalleDevolucionProveedor detalle) throws SQLException {
        String sql = "INSERT INTO detalle_devolucion_proveedor (id_devolucion_proveedor, producto_id, cantidad) " +
                    "VALUES (?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, detalle.getIdDevolucionProveedor());
            stmt.setInt(2, detalle.getProductoId());
            stmt.setInt(3, detalle.getCantidad());
            
            stmt.executeUpdate();
        }
    }
    
    public void registrarDevolucionCompleta(int proveedorId, int usuarioId, String productos) throws SQLException {
        // Crear la devolucion
        DevolucionProveedor devolucion = new DevolucionProveedor();
        devolucion.setFechaDevolucion(new Date(System.currentTimeMillis()));
        devolucion.setObservacion("Devolucion registrada por email");
        devolucion.setProveedorId(proveedorId);
        devolucion.setUsuarioId(usuarioId);
        
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
            DetalleDevolucionProveedor detalle = new DetalleDevolucionProveedor();
            detalle.setIdDevolucionProveedor(devolucionId);
            detalle.setProductoId(productoId);
            detalle.setCantidad(cantidad);
            insertarDetalle(detalle);
            
            // Aumentar stock del producto
            var producto = productoService.buscarPorId(productoId);
            if (producto != null) {
                int nuevoStock = producto.getStockActual() + cantidad;
                productoService.actualizarStock(productoId, nuevoStock);
            }
        }
    }
    
    public List<DevolucionProveedor> listarTodas() throws SQLException {
        List<DevolucionProveedor> devoluciones = new ArrayList<>();
        String sql = "SELECT id, fecha_devolucion, observacion, proveedor_id, usuario_id " +
                    "FROM devolucion_proveedor ORDER BY fecha_devolucion DESC";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                devoluciones.add(new DevolucionProveedor(
                    rs.getInt("id"),
                    rs.getDate("fecha_devolucion"),
                    rs.getString("observacion"),
                    (Integer) rs.getObject("proveedor_id"),
                    (Integer) rs.getObject("usuario_id")
                ));
            }
        }
        return devoluciones;
    }
    
    public List<DetalleDevolucionProveedor> listarDetalles(int devolucionId) throws SQLException {
        List<DetalleDevolucionProveedor> detalles = new ArrayList<>();
        String sql = "SELECT id, id_devolucion_proveedor, producto_id, cantidad " +
                    "FROM detalle_devolucion_proveedor WHERE id_devolucion_proveedor = ?";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, devolucionId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                detalles.add(new DetalleDevolucionProveedor(
                    rs.getInt("id"),
                    rs.getInt("id_devolucion_proveedor"),
                    rs.getInt("producto_id"),
                    rs.getInt("cantidad")
                ));
            }
        }
        return detalles;
    }
}
