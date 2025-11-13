package bo.tecnoweb.emailsystem.service;

import bo.tecnoweb.emailsystem.config.Config;
import bo.tecnoweb.emailsystem.model.Venta;
import bo.tecnoweb.emailsystem.model.DetalleVenta;
import bo.tecnoweb.emailsystem.model.Pago;
import bo.tecnoweb.emailsystem.model.Producto;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaService {
    
    private ProductoService productoService;
    
    public VentaService() {
        this.productoService = new ProductoService();
    }
    
    public int insertar(Venta venta) throws SQLException {
        String sql = "INSERT INTO venta (fecha_venta, tipo, total, estado, cliente_id, vendedor_id, cotizacion_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, venta.getFechaVenta());
            stmt.setString(2, venta.getTipo());
            stmt.setBigDecimal(3, venta.getTotal());
            stmt.setString(4, venta.getEstado());
            
            if (venta.getClienteId() != null) {
                stmt.setInt(5, venta.getClienteId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            if (venta.getVendedorId() != null) {
                stmt.setInt(6, venta.getVendedorId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            
            if (venta.getCotizacionId() != null) {
                stmt.setInt(7, venta.getCotizacionId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("No se pudo obtener el ID de la venta");
        }
    }
    
    public void insertarDetalle(DetalleVenta detalle) throws SQLException {
        String sql = "INSERT INTO detalle_venta (cantidad, precio_unitario, subtotal, venta_id, producto_id) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, detalle.getCantidad());
            stmt.setBigDecimal(2, detalle.getPrecioUnitario());
            stmt.setBigDecimal(3, detalle.getSubtotal());
            stmt.setInt(4, detalle.getVentaId());
            stmt.setInt(5, detalle.getProductoId());
            
            stmt.executeUpdate();
        }
    }
    
    public void insertarPago(Pago pago) throws SQLException {
        String sql = "INSERT INTO pago (monto, metodo, fecha_pago, venta_id) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, pago.getMonto());
            stmt.setString(2, pago.getMetodo());
            stmt.setDate(3, pago.getFechaPago());
            stmt.setInt(4, pago.getVentaId());
            
            stmt.executeUpdate();
        }
    }
    
    public Venta buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, fecha_venta, tipo, total, estado, cliente_id, vendedor_id, cotizacion_id " +
                    "FROM venta WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Venta(
                    rs.getInt("id"),
                    rs.getDate("fecha_venta"),
                    rs.getString("tipo"),
                    rs.getBigDecimal("total"),
                    rs.getString("estado"),
                    (Integer) rs.getObject("cliente_id"),
                    (Integer) rs.getObject("vendedor_id"),
                    (Integer) rs.getObject("cotizacion_id")
                );
            }
            return null;
        }
    }
    
    public List<Venta> listarTodas() throws SQLException {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT id, fecha_venta, tipo, total, estado, cliente_id, vendedor_id, cotizacion_id " +
                    "FROM venta ORDER BY id DESC";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                ventas.add(new Venta(
                    rs.getInt("id"),
                    rs.getDate("fecha_venta"),
                    rs.getString("tipo"),
                    rs.getBigDecimal("total"),
                    rs.getString("estado"),
                    (Integer) rs.getObject("cliente_id"),
                    (Integer) rs.getObject("vendedor_id"),
                    (Integer) rs.getObject("cotizacion_id")
                ));
            }
        }
        return ventas;
    }
    
    public List<Venta> listarPorVendedor(int vendedorId) throws SQLException {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT id, fecha_venta, tipo, total, estado, cliente_id, vendedor_id, cotizacion_id " +
                    "FROM venta WHERE vendedor_id = ? ORDER BY id DESC";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, vendedorId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ventas.add(new Venta(
                    rs.getInt("id"),
                    rs.getDate("fecha_venta"),
                    rs.getString("tipo"),
                    rs.getBigDecimal("total"),
                    rs.getString("estado"),
                    (Integer) rs.getObject("cliente_id"),
                    (Integer) rs.getObject("vendedor_id"),
                    (Integer) rs.getObject("cotizacion_id")
                ));
            }
        }
        return ventas;
    }
    
    public List<Venta> listarPorVendedorYFecha(int vendedorId, Date fecha) throws SQLException {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT id, fecha_venta, tipo, total, estado, cliente_id, vendedor_id, cotizacion_id " +
                    "FROM venta WHERE vendedor_id = ? AND fecha_venta = ? ORDER BY fecha_venta DESC";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, vendedorId);
            stmt.setDate(2, fecha);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ventas.add(new Venta(
                    rs.getInt("id"),
                    rs.getDate("fecha_venta"),
                    rs.getString("tipo"),
                    rs.getBigDecimal("total"),
                    rs.getString("estado"),
                    (Integer) rs.getObject("cliente_id"),
                    (Integer) rs.getObject("vendedor_id"),
                    (Integer) rs.getObject("cotizacion_id")
                ));
            }
        }
        return ventas;
    }
    
    public List<Venta> listarPendientes() throws SQLException {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT id, fecha_venta, tipo, total, estado, cliente_id, vendedor_id, cotizacion_id " +
                    "FROM venta WHERE estado = 'pendiente' ORDER BY fecha_venta DESC";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                ventas.add(new Venta(
                    rs.getInt("id"),
                    rs.getDate("fecha_venta"),
                    rs.getString("tipo"),
                    rs.getBigDecimal("total"),
                    rs.getString("estado"),
                    (Integer) rs.getObject("cliente_id"),
                    (Integer) rs.getObject("vendedor_id"),
                    (Integer) rs.getObject("cotizacion_id")
                ));
            }
        }
        return ventas;
    }
    
    public List<Venta> listarPorCliente(int clienteId) throws SQLException {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT id, fecha_venta, tipo, total, estado, cliente_id, vendedor_id, cotizacion_id " +
                    "FROM venta WHERE cliente_id = ? ORDER BY fecha_venta DESC";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, clienteId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ventas.add(new Venta(
                    rs.getInt("id"),
                    rs.getDate("fecha_venta"),
                    rs.getString("tipo"),
                    rs.getBigDecimal("total"),
                    rs.getString("estado"),
                    (Integer) rs.getObject("cliente_id"),
                    (Integer) rs.getObject("vendedor_id"),
                    (Integer) rs.getObject("cotizacion_id")
                ));
            }
        }
        return ventas;
    }
    
    public List<Venta> listarPendientesPorCliente(int clienteId) throws SQLException {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT id, fecha_venta, tipo, total, estado, cliente_id, vendedor_id, cotizacion_id " +
                    "FROM venta WHERE cliente_id = ? AND estado = 'pendiente' ORDER BY fecha_venta DESC";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, clienteId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ventas.add(new Venta(
                    rs.getInt("id"),
                    rs.getDate("fecha_venta"),
                    rs.getString("tipo"),
                    rs.getBigDecimal("total"),
                    rs.getString("estado"),
                    (Integer) rs.getObject("cliente_id"),
                    (Integer) rs.getObject("vendedor_id"),
                    (Integer) rs.getObject("cotizacion_id")
                ));
            }
        }
        return ventas;
    }
    
    public BigDecimal calcularSaldoPendiente(int ventaId) throws SQLException {
        Venta venta = buscarPorId(ventaId);
        if (venta == null) {
            throw new SQLException("No existe la venta con ID: " + ventaId);
        }
        
        List<Pago> pagos = listarPagos(ventaId);
        BigDecimal totalPagado = BigDecimal.ZERO;
        
        for (Pago pago : pagos) {
            totalPagado = totalPagado.add(pago.getMonto());
        }
        
        return venta.getTotal().subtract(totalPagado);
    }
    
    public List<DetalleVenta> listarDetalles(int ventaId) throws SQLException {
        List<DetalleVenta> detalles = new ArrayList<>();
        String sql = "SELECT id, cantidad, precio_unitario, subtotal, venta_id, producto_id " +
                    "FROM detalle_venta WHERE venta_id = ?";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, ventaId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                detalles.add(new DetalleVenta(
                    rs.getInt("id"),
                    rs.getInt("cantidad"),
                    rs.getBigDecimal("precio_unitario"),
                    rs.getBigDecimal("subtotal"),
                    rs.getInt("venta_id"),
                    rs.getInt("producto_id")
                ));
            }
        }
        return detalles;
    }
    
    public List<Pago> listarPagos(int ventaId) throws SQLException {
        List<Pago> pagos = new ArrayList<>();
        String sql = "SELECT id, monto, metodo, fecha_pago, venta_id " +
                    "FROM pago WHERE venta_id = ?";
        
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
    
    public boolean actualizar(Venta venta) throws SQLException {
        String sql = "UPDATE venta SET fecha_venta = ?, tipo = ?, total = ?, estado = ?, " +
                    "cliente_id = ?, vendedor_id = ?, cotizacion_id = ? WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(
                Config.getDbUrl(), Config.getDbUser(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, venta.getFechaVenta());
            stmt.setString(2, venta.getTipo());
            stmt.setBigDecimal(3, venta.getTotal());
            stmt.setString(4, venta.getEstado());
            
            if (venta.getClienteId() != null) {
                stmt.setInt(5, venta.getClienteId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            if (venta.getVendedorId() != null) {
                stmt.setInt(6, venta.getVendedorId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            
            if (venta.getCotizacionId() != null) {
                stmt.setInt(7, venta.getCotizacionId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }
            
            stmt.setInt(8, venta.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public void crearVentaContado(int clienteId, int vendedorId, String productos, String metodoPago) throws SQLException {
        // Crear la venta
        Venta venta = new Venta();
        venta.setFechaVenta(new Date(System.currentTimeMillis()));
        venta.setTipo("contado");
        venta.setEstado("pagada");
        venta.setClienteId(clienteId);
        venta.setVendedorId(vendedorId);
        
        BigDecimal totalGeneral = BigDecimal.ZERO;
        List<DetalleVenta> detalles = new ArrayList<>();
        
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
            Producto producto = productoService.buscarPorId(productoId);
            if (producto == null) {
                throw new SQLException("No existe el producto con ID: " + productoId);
            }
            
            BigDecimal precio = producto.getPrecioUnitario();
            BigDecimal subtotal = precio.multiply(new BigDecimal(cantidad));
            
            totalGeneral = totalGeneral.add(subtotal);
            
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProductoId(productoId);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precio);
            detalle.setSubtotal(subtotal);
            
            detalles.add(detalle);
        }
        
        venta.setTotal(totalGeneral);
        int ventaId = insertar(venta);
        
        // Insertar detalles y reducir stock
        for (DetalleVenta detalle : detalles) {
            detalle.setVentaId(ventaId);
            insertarDetalle(detalle);
            
            // Reducir stock
            Producto producto = productoService.buscarPorId(detalle.getProductoId());
            if (producto != null) {
                int nuevoStock = producto.getStockActual() - detalle.getCantidad();
                productoService.actualizarStock(detalle.getProductoId(), nuevoStock);
            }
        }
        
        // Registrar pago completo
        Pago pago = new Pago();
        pago.setMonto(totalGeneral);
        pago.setMetodo(metodoPago);
        pago.setFechaPago(new Date(System.currentTimeMillis()));
        pago.setVentaId(ventaId);
        insertarPago(pago);
    }
    
    public void crearVentaCredito(int clienteId, int vendedorId, String productos, BigDecimal montoInicial, String metodoPago) throws SQLException {
        // Crear la venta
        Venta venta = new Venta();
        venta.setFechaVenta(new Date(System.currentTimeMillis()));
        venta.setTipo("credito");
        venta.setClienteId(clienteId);
        venta.setVendedorId(vendedorId);
        
        BigDecimal totalGeneral = BigDecimal.ZERO;
        List<DetalleVenta> detalles = new ArrayList<>();
        
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
            Producto producto = productoService.buscarPorId(productoId);
            if (producto == null) {
                throw new SQLException("No existe el producto con ID: " + productoId);
            }
            
            BigDecimal precio = producto.getPrecioUnitario();
            BigDecimal subtotal = precio.multiply(new BigDecimal(cantidad));
            
            totalGeneral = totalGeneral.add(subtotal);
            
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProductoId(productoId);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precio);
            detalle.setSubtotal(subtotal);
            
            detalles.add(detalle);
        }
        
        venta.setTotal(totalGeneral);
        
        // Determinar estado según monto inicial
        if (montoInicial.compareTo(totalGeneral) >= 0) {
            venta.setEstado("pagada");
        } else {
            venta.setEstado("pendiente");
        }
        
        int ventaId = insertar(venta);
        
        // Insertar detalles y reducir stock
        for (DetalleVenta detalle : detalles) {
            detalle.setVentaId(ventaId);
            insertarDetalle(detalle);
            
            // Reducir stock
            Producto producto = productoService.buscarPorId(detalle.getProductoId());
            if (producto != null) {
                int nuevoStock = producto.getStockActual() - detalle.getCantidad();
                productoService.actualizarStock(detalle.getProductoId(), nuevoStock);
            }
        }
        
        // Registrar pago inicial
        Pago pago = new Pago();
        pago.setMonto(montoInicial);
        pago.setMetodo(metodoPago);
        pago.setFechaPago(new Date(System.currentTimeMillis()));
        pago.setVentaId(ventaId);
        insertarPago(pago);
    }
    
    public void abonarVenta(int ventaId, BigDecimal monto, String metodo) throws SQLException {
        // Registrar el pago
        Pago pago = new Pago();
        pago.setMonto(monto);
        pago.setMetodo(metodo);
        pago.setFechaPago(new Date(System.currentTimeMillis()));
        pago.setVentaId(ventaId);
        insertarPago(pago);
        
        // Verificar si se completó el total
        Venta venta = buscarPorId(ventaId);
        if (venta != null) {
            List<Pago> pagos = listarPagos(ventaId);
            BigDecimal totalPagado = BigDecimal.ZERO;
            
            for (Pago p : pagos) {
                totalPagado = totalPagado.add(p.getMonto());
            }
            
            // Si se completó el total, marcar como pagada
            if (totalPagado.compareTo(venta.getTotal()) >= 0) {
                venta.setEstado("pagada");
                actualizar(venta);
            }
        }
    }
}
