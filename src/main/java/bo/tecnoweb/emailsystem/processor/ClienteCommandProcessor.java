package bo.tecnoweb.emailsystem.processor;

import bo.tecnoweb.emailsystem.model.*;
import bo.tecnoweb.emailsystem.service.*;
import bo.tecnoweb.emailsystem.util.ResponseFormatter;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ClienteCommandProcessor {
    
    private ProductoService productoService;
    private CotizacionService cotizacionService;
    private VentaService ventaService;
    private PagoService pagoService;
    private DevolucionService devolucionService;
    
    public ClienteCommandProcessor() {
        this.productoService = new ProductoService();
        this.cotizacionService = new CotizacionService();
        this.ventaService = new VentaService();
        this.pagoService = new PagoService();
        this.devolucionService = new DevolucionService();
    }
    
    public String procesarComando(String comando, List<String> parametros, Usuario cliente) {
        try {
            switch (comando) {
                // ===== AYUDA =====
                case "HELP":
                    return mostrarHelp();
                    
                // ===== CONSULTA DE PRODUCTOS =====
                case "CATALOGO":
                    return catalogo(parametros);
                    
                case "BUSCARPRODUCTO":
                    return buscarProducto(parametros);
                    
                case "VERPRODUCTO":
                    return verProducto(parametros);
                    
                // ===== MIS COTIZACIONES =====
                case "MISCOTIZACIONES":
                    return misCotizaciones(cliente);
                    
                case "VERCOTIZACION":
                    return verCotizacion(parametros);
                    
                // ===== MIS COMPRAS =====
                case "MISCOMPRAS":
                    return misCompras(cliente);
                    
                case "MISCOMPRASPENDIENTES":
                    return misComprasPendientes(cliente);
                    
                case "VERCOMPRA":
                    return verCompra(parametros);
                    
                case "MISALDO":
                    return miSaldo(parametros);
                    
                // ===== MIS PAGOS =====
                case "MISPAGOS":
                    return misPagos(cliente);
                    
                case "PAGOSDECOMPRA":
                    return pagosDeCompra(parametros);
                    
                // ===== MIS DEVOLUCIONES =====
                case "MISDEVOLUCIONES":
                    return misDevoluciones(cliente);
                    
                case "VERDEVOLUCION":
                    return verDevolucion(parametros);
                    
                default:
                    return ResponseFormatter.error("Comando no reconocido para cliente: " + comando);
            }
        } catch (Exception e) {
            return ResponseFormatter.error("Error al procesar comando: " + e.getMessage());
        }
    }
    
    // ===== IMPLEMENTACIÓN DE COMANDOS =====
    
    private String catalogo(List<String> params) throws SQLException {
        List<Producto> productos;
        
        if (params.isEmpty()) {
            productos = productoService.listarTodos();
        } else {
            String categoria = params.get(0);
            productos = productoService.listarPorNombreCategoria(categoria);
        }
        
        if (productos.isEmpty()) {
            return ResponseFormatter.success("Catálogo de productos", "No hay productos disponibles");
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>");
        html.append("<tr style='background-color: #2196F3; color: white;'>");
        html.append("<th>ID</th><th>Nombre</th><th>Descripción</th><th>Precio</th><th>Stock</th></tr>");
        
        for (Producto producto : productos) {
            if (producto.getStockActual() > 0) { // Solo mostrar con stock
                html.append("<tr>");
                html.append("<td>").append(producto.getId()).append("</td>");
                html.append("<td>").append(producto.getNombre()).append("</td>");
                html.append("<td>").append(producto.getDescripcion()).append("</td>");
                html.append("<td>Bs ").append(String.format("%.2f", producto.getPrecioUnitario())).append("</td>");
                html.append("<td>").append(producto.getStockActual()).append("</td>");
                html.append("</tr>");
            }
        }
        html.append("</table>");
        
        return ResponseFormatter.success("Catálogo de productos", html.toString());
    }
    
    private String buscarProducto(List<String> params) throws SQLException {
        if (params.size() != 1) {
            return ResponseFormatter.error("Parámetros incorrectos. Use: BUSCARPRODUCTO[\"nombre\"]");
        }
        
        String nombre = params.get(0);
        Producto producto = productoService.buscarPorNombre(nombre);
        
        if (producto == null) {
            return ResponseFormatter.success("Búsqueda de productos", "No se encontró producto con: " + nombre);
        }
        
        if (producto.getStockActual() == 0) {
            return ResponseFormatter.success("Producto encontrado", 
                producto.getNombre() + "<br><b>SIN STOCK DISPONIBLE</b>");
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<h3>").append(producto.getNombre()).append("</h3>");
        html.append("<p><b>Descripción:</b> ").append(producto.getDescripcion()).append("</p>");
        html.append("<p><b>Precio:</b> Bs ").append(String.format("%.2f", producto.getPrecioUnitario())).append("</p>");
        html.append("<p><b>Stock disponible:</b> ").append(producto.getStockActual()).append(" unidades</p>");
        html.append("<p><b>ID:</b> ").append(producto.getId()).append("</p>");
        
        return ResponseFormatter.success("Detalle del producto", html.toString());
    }
    
    private String verProducto(List<String> params) throws SQLException {
        if (params.size() != 1) {
            return ResponseFormatter.error("Parámetros incorrectos. Use: VERPRODUCTO[\"id\"]");
        }
        
        int productoId = Integer.parseInt(params.get(0));
        Producto producto = productoService.buscarPorId(productoId);
        
        if (producto == null) {
            return ResponseFormatter.error("No existe producto con ID: " + productoId);
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<h3>").append(producto.getNombre()).append("</h3>");
        html.append("<p><b>Descripción:</b> ").append(producto.getDescripcion()).append("</p>");
        html.append("<p><b>Precio:</b> Bs ").append(String.format("%.2f", producto.getPrecioUnitario())).append("</p>");
        html.append("<p><b>Stock disponible:</b> ").append(producto.getStockActual()).append(" unidades</p>");
        
        String disponibilidad = producto.getStockActual() > 0 
            ? "<span style='color: green;'>DISPONIBLE</span>" 
            : "<span style='color: red;'>AGOTADO</span>";
        html.append("<p><b>Estado:</b> ").append(disponibilidad).append("</p>");
        
        return ResponseFormatter.success("Detalle del producto", html.toString());
    }
    
    private String misCotizaciones(Usuario cliente) throws SQLException {
        List<Cotizacion> cotizaciones = cotizacionService.listarPorCliente(cliente.getId());
        
        if (cotizaciones.isEmpty()) {
            return ResponseFormatter.success("Mis cotizaciones", "No tiene cotizaciones registradas");
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>");
        html.append("<tr style='background-color: #FF9800; color: white;'>");
        html.append("<th>ID</th><th>Fecha</th><th>Total</th></tr>");
        
        for (Cotizacion cot : cotizaciones) {
            html.append("<tr>");
            html.append("<td>").append(cot.getId()).append("</td>");
            html.append("<td>").append(cot.getFechaCotizacion()).append("</td>");
            html.append("<td>Bs ").append(String.format("%.2f", cot.getTotal())).append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        html.append("<p><i>Use VERCOTIZACION[\"id\"] para ver detalles</i></p>");
        
        return ResponseFormatter.success("Mis cotizaciones (" + cotizaciones.size() + ")", html.toString());
    }
    
    private String verCotizacion(List<String> params) throws SQLException {
        if (params.size() != 1) {
            return ResponseFormatter.error("Parámetros incorrectos. Use: VERCOTIZACION[\"id\"]");
        }
        
        int cotizacionId = Integer.parseInt(params.get(0));
        Cotizacion cotizacion = cotizacionService.buscarPorId(cotizacionId);
        
        if (cotizacion == null) {
            return ResponseFormatter.error("No existe cotización con ID: " + cotizacionId);
        }
        
        List<DetalleCotizacion> detalles = cotizacionService.listarDetalles(cotizacionId);
        
        StringBuilder html = new StringBuilder();
        html.append("<h3>Cotización #").append(cotizacionId).append("</h3>");
        html.append("<p><b>Fecha:</b> ").append(cotizacion.getFechaCotizacion()).append("</p>");
        
        html.append("<h4>Productos cotizados:</h4>");
        html.append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>");
        html.append("<tr style='background-color: #FF9800; color: white;'>");
        html.append("<th>Producto ID</th><th>Cantidad</th><th>Precio Unit.</th><th>Subtotal</th></tr>");
        
        for (DetalleCotizacion det : detalles) {
            html.append("<tr>");
            html.append("<td>").append(det.getProductoId()).append("</td>");
            html.append("<td>").append(det.getCantidad()).append("</td>");
            html.append("<td>Bs ").append(String.format("%.2f", det.getCostoUnitario())).append("</td>");
            html.append("<td>Bs ").append(String.format("%.2f", det.getSubtotal())).append("</td>");
            html.append("</tr>");
        }
        html.append("<tr style='background-color: #f0f0f0; font-weight: bold;'>");
        html.append("<td colspan='3' align='right'>TOTAL:</td>");
        html.append("<td>Bs ").append(String.format("%.2f", cotizacion.getTotal())).append("</td>");
        html.append("</tr></table>");
        
        return ResponseFormatter.success("Detalle de cotización", html.toString());
    }
    
    private String misCompras(Usuario cliente) throws SQLException {
        List<Venta> ventas = ventaService.listarPorCliente(cliente.getId());
        
        if (ventas.isEmpty()) {
            return ResponseFormatter.success("Mis compras", "No tiene compras registradas");
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>");
        html.append("<tr style='background-color: #4CAF50; color: white;'>");
        html.append("<th>ID</th><th>Fecha</th><th>Tipo</th><th>Total</th><th>Estado</th></tr>");
        
        for (Venta venta : ventas) {
            String colorEstado = venta.getEstado().equals("pagada") ? "green" : "orange";
            html.append("<tr>");
            html.append("<td>").append(venta.getId()).append("</td>");
            html.append("<td>").append(venta.getFechaVenta()).append("</td>");
            html.append("<td>").append(venta.getTipo().toUpperCase()).append("</td>");
            html.append("<td>Bs ").append(String.format("%.2f", venta.getTotal())).append("</td>");
            html.append("<td style='color: ").append(colorEstado).append(";'><b>")
                .append(venta.getEstado().toUpperCase()).append("</b></td>");
            html.append("</tr>");
        }
        html.append("</table>");
        html.append("<p><i>Use VERCOMPRA[\"id\"] para ver detalles</i></p>");
        
        return ResponseFormatter.success("Mis compras (" + ventas.size() + ")", html.toString());
    }
    
    private String misComprasPendientes(Usuario cliente) throws SQLException {
        List<Venta> ventas = ventaService.listarPendientesPorCliente(cliente.getId());
        
        if (ventas.isEmpty()) {
            return ResponseFormatter.success("Compras pendientes", "No tiene compras pendientes de pago");
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>");
        html.append("<tr style='background-color: #FF9800; color: white;'>");
        html.append("<th>ID</th><th>Fecha</th><th>Total</th><th>Saldo Pendiente</th></tr>");
        
        for (Venta venta : ventas) {
            BigDecimal saldo = ventaService.calcularSaldoPendiente(venta.getId());
            html.append("<tr>");
            html.append("<td>").append(venta.getId()).append("</td>");
            html.append("<td>").append(venta.getFechaVenta()).append("</td>");
            html.append("<td>Bs ").append(String.format("%.2f", venta.getTotal())).append("</td>");
            html.append("<td style='color: red;'><b>Bs ").append(String.format("%.2f", saldo)).append("</b></td>");
            html.append("</tr>");
        }
        html.append("</table>");
        html.append("<p><i>Use MISALDO[\"venta_id\"] para ver cuánto falta pagar</i></p>");
        
        return ResponseFormatter.success("Compras pendientes (" + ventas.size() + ")", html.toString());
    }
    
    private String verCompra(List<String> params) throws SQLException {
        if (params.size() != 1) {
            return ResponseFormatter.error("Parámetros incorrectos. Use: VERCOMPRA[\"id\"]");
        }
        
        int ventaId = Integer.parseInt(params.get(0));
        Venta venta = ventaService.buscarPorId(ventaId);
        
        if (venta == null) {
            return ResponseFormatter.error("No existe compra con ID: " + ventaId);
        }
        
        List<DetalleVenta> detalles = ventaService.listarDetalles(ventaId);
        List<Pago> pagos = ventaService.listarPagos(ventaId);
        
        StringBuilder html = new StringBuilder();
        html.append("<h3>Compra #").append(ventaId).append("</h3>");
        html.append("<p><b>Fecha:</b> ").append(venta.getFechaVenta()).append("</p>");
        html.append("<p><b>Tipo:</b> ").append(venta.getTipo().toUpperCase()).append("</p>");
        html.append("<p><b>Estado:</b> ").append(venta.getEstado().toUpperCase()).append("</p>");
        
        html.append("<h4>Productos comprados:</h4>");
        html.append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>");
        html.append("<tr style='background-color: #4CAF50; color: white;'>");
        html.append("<th>Producto ID</th><th>Cantidad</th><th>Precio Unit.</th><th>Subtotal</th></tr>");
        
        for (DetalleVenta det : detalles) {
            html.append("<tr>");
            html.append("<td>").append(det.getProductoId()).append("</td>");
            html.append("<td>").append(det.getCantidad()).append("</td>");
            html.append("<td>Bs ").append(String.format("%.2f", det.getPrecioUnitario())).append("</td>");
            html.append("<td>Bs ").append(String.format("%.2f", det.getSubtotal())).append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        
        if (!pagos.isEmpty()) {
            html.append("<h4>Pagos realizados:</h4>");
            html.append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>");
            html.append("<tr style='background-color: #2196F3; color: white;'>");
            html.append("<th>Fecha</th><th>Monto</th><th>Método</th></tr>");
            
            for (Pago pago : pagos) {
                html.append("<tr>");
                html.append("<td>").append(pago.getFechaPago()).append("</td>");
                html.append("<td>Bs ").append(String.format("%.2f", pago.getMonto())).append("</td>");
                html.append("<td>").append(pago.getMetodo().toUpperCase()).append("</td>");
                html.append("</tr>");
            }
            html.append("</table>");
        }
        
        html.append("<p style='font-size: 18px;'><b>TOTAL: Bs ").append(String.format("%.2f", venta.getTotal())).append("</b></p>");
        
        if (venta.getEstado().equals("pendiente")) {
            BigDecimal saldo = ventaService.calcularSaldoPendiente(ventaId);
            html.append("<p style='color: red; font-size: 16px;'><b>SALDO PENDIENTE: Bs ")
                .append(String.format("%.2f", saldo)).append("</b></p>");
        }
        
        return ResponseFormatter.success("Detalle de compra", html.toString());
    }
    
    private String miSaldo(List<String> params) throws SQLException {
        if (params.size() != 1) {
            return ResponseFormatter.error("Parámetros incorrectos. Use: MISALDO[\"venta_id\"]");
        }
        
        int ventaId = Integer.parseInt(params.get(0));
        Venta venta = ventaService.buscarPorId(ventaId);
        
        if (venta == null) {
            return ResponseFormatter.error("No existe venta con ID: " + ventaId);
        }
        
        BigDecimal saldo = ventaService.calcularSaldoPendiente(ventaId);
        
        StringBuilder html = new StringBuilder();
        html.append("<h3>Saldo de Compra #").append(ventaId).append("</h3>");
        html.append("<p><b>Total de la compra:</b> Bs ").append(String.format("%.2f", venta.getTotal())).append("</p>");
        html.append("<p><b>Saldo pendiente:</b> <span style='color: red; font-size: 20px;'><b>Bs ")
            .append(String.format("%.2f", saldo)).append("</b></span></p>");
        
        if (saldo.compareTo(BigDecimal.ZERO) == 0) {
            html.append("<p style='color: green;'><b>✓ COMPRA PAGADA COMPLETAMENTE</b></p>");
        } else {
            html.append("<p><i>Contacte a su vendedor para realizar un abono</i></p>");
        }
        
        return ResponseFormatter.success("Saldo pendiente", html.toString());
    }
    
    private String misPagos(Usuario cliente) throws SQLException {
        List<Pago> pagos = pagoService.listarPorCliente(cliente.getId());
        
        if (pagos.isEmpty()) {
            return ResponseFormatter.success("Mis pagos", "No tiene pagos registrados");
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>");
        html.append("<tr style='background-color: #2196F3; color: white;'>");
        html.append("<th>ID Pago</th><th>Fecha</th><th>Monto</th><th>Método</th><th>Venta ID</th></tr>");
        
        BigDecimal totalPagado = BigDecimal.ZERO;
        for (Pago pago : pagos) {
            html.append("<tr>");
            html.append("<td>").append(pago.getId()).append("</td>");
            html.append("<td>").append(pago.getFechaPago()).append("</td>");
            html.append("<td>Bs ").append(String.format("%.2f", pago.getMonto())).append("</td>");
            html.append("<td>").append(pago.getMetodo().toUpperCase()).append("</td>");
            html.append("<td>").append(pago.getVentaId()).append("</td>");
            html.append("</tr>");
            totalPagado = totalPagado.add(pago.getMonto());
        }
        html.append("<tr style='background-color: #f0f0f0; font-weight: bold;'>");
        html.append("<td colspan='2' align='right'>TOTAL PAGADO:</td>");
        html.append("<td colspan='3'>Bs ").append(String.format("%.2f", totalPagado)).append("</td>");
        html.append("</tr></table>");
        
        return ResponseFormatter.success("Historial de pagos (" + pagos.size() + ")", html.toString());
    }
    
    private String pagosDeCompra(List<String> params) throws SQLException {
        if (params.size() != 1) {
            return ResponseFormatter.error("Parámetros incorrectos. Use: PAGOSDECOMPRA[\"venta_id\"]");
        }
        
        int ventaId = Integer.parseInt(params.get(0));
        Venta venta = ventaService.buscarPorId(ventaId);
        
        if (venta == null) {
            return ResponseFormatter.error("No existe venta con ID: " + ventaId);
        }
        
        List<Pago> pagos = pagoService.listarPorVenta(ventaId);
        
        if (pagos.isEmpty()) {
            return ResponseFormatter.success("Pagos de la compra #" + ventaId, "No hay pagos registrados para esta compra");
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<h3>Pagos de Compra #").append(ventaId).append("</h3>");
        html.append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>");
        html.append("<tr style='background-color: #2196F3; color: white;'>");
        html.append("<th>Fecha</th><th>Monto</th><th>Método</th></tr>");
        
        BigDecimal totalPagado = BigDecimal.ZERO;
        for (Pago pago : pagos) {
            html.append("<tr>");
            html.append("<td>").append(pago.getFechaPago()).append("</td>");
            html.append("<td>Bs ").append(String.format("%.2f", pago.getMonto())).append("</td>");
            html.append("<td>").append(pago.getMetodo().toUpperCase()).append("</td>");
            html.append("</tr>");
            totalPagado = totalPagado.add(pago.getMonto());
        }
        html.append("<tr style='background-color: #f0f0f0; font-weight: bold;'>");
        html.append("<td align='right'>TOTAL PAGADO:</td>");
        html.append("<td colspan='2'>Bs ").append(String.format("%.2f", totalPagado)).append("</td>");
        html.append("</tr></table>");
        
        BigDecimal saldo = venta.getTotal().subtract(totalPagado);
        html.append("<p><b>Total de la compra:</b> Bs ").append(String.format("%.2f", venta.getTotal())).append("</p>");
        html.append("<p><b>Saldo restante:</b> <span style='color: ")
            .append(saldo.compareTo(BigDecimal.ZERO) == 0 ? "green" : "red")
            .append(";'><b>Bs ").append(String.format("%.2f", saldo)).append("</b></span></p>");
        
        return ResponseFormatter.success("Detalle de pagos", html.toString());
    }
    
    private String misDevoluciones(Usuario cliente) throws SQLException {
        List<Devolucion> devoluciones = devolucionService.listarPorCliente(cliente.getId());
        
        if (devoluciones.isEmpty()) {
            return ResponseFormatter.success("Mis devoluciones", "No tiene devoluciones registradas");
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>");
        html.append("<tr style='background-color: #f44336; color: white;'>");
        html.append("<th>ID</th><th>Fecha</th><th>Venta ID</th><th>Motivo</th></tr>");
        
        for (Devolucion dev : devoluciones) {
            html.append("<tr>");
            html.append("<td>").append(dev.getId()).append("</td>");
            html.append("<td>").append(dev.getFechaDevolucion()).append("</td>");
            html.append("<td>").append(dev.getVentaId()).append("</td>");
            html.append("<td>").append(dev.getMotivo()).append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        html.append("<p><i>Use VERDEVOLUCION[\"id\"] para ver detalles</i></p>");
        
        return ResponseFormatter.success("Mis devoluciones (" + devoluciones.size() + ")", html.toString());
    }
    
    private String verDevolucion(List<String> params) throws SQLException {
        if (params.size() != 1) {
            return ResponseFormatter.error("Parámetros incorrectos. Use: VERDEVOLUCION[\"id\"]");
        }
        
        int devolucionId = Integer.parseInt(params.get(0));
        Devolucion devolucion = devolucionService.buscarPorId(devolucionId);
        
        if (devolucion == null) {
            return ResponseFormatter.error("No existe devolución con ID: " + devolucionId);
        }
        
        List<DetalleDevolucionCliente> detalles = devolucionService.listarDetalles(devolucionId);
        
        StringBuilder html = new StringBuilder();
        html.append("<h3>Devolución #").append(devolucionId).append("</h3>");
        html.append("<p><b>Fecha:</b> ").append(devolucion.getFechaDevolucion()).append("</p>");
        html.append("<p><b>Venta ID:</b> ").append(devolucion.getVentaId()).append("</p>");
        html.append("<p><b>Motivo:</b> ").append(devolucion.getMotivo()).append("</p>");
        
        html.append("<h4>Productos devueltos:</h4>");
        html.append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>");
        html.append("<tr style='background-color: #f44336; color: white;'>");
        html.append("<th>Producto ID</th><th>Cantidad</th></tr>");
        
        for (DetalleDevolucionCliente det : detalles) {
            html.append("<tr>");
            html.append("<td>").append(det.getProductoId()).append("</td>");
            html.append("<td>").append(det.getCantidad()).append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        
        return ResponseFormatter.success("Detalle de devolución", html.toString());
    }
    
    // ==================== AYUDA ====================
    
    private String mostrarHelp() {
        StringBuilder help = new StringBuilder();
        help.append("<div style='font-family: Arial, sans-serif;'>");
        help.append("<h2 style='color: #2c3e50;'>COMANDOS DISPONIBLES - CLIENTE</h2>");
        
        help.append("<h3 style='color: #e74c3c;'>CONSULTA DE PRODUCTOS</h3>");
        help.append("<ul>");
        help.append("<li><strong>CATALOGO[]</strong> - Ver catálogo completo de productos</li>");
        help.append("<li><strong>CATALOGO[\"categoria\"]</strong> - Ver productos por categoría</li>");
        help.append("<li><strong>BUSCARPRODUCTO[\"nombre\"]</strong> - Buscar producto por nombre</li>");
        help.append("<li><strong>VERPRODUCTO[\"id\"]</strong> - Ver detalles de un producto</li>");
        help.append("</ul>");
        
        help.append("<h3 style='color: #e74c3c;'>MIS COTIZACIONES</h3>");
        help.append("<ul>");
        help.append("<li><strong>MISCOTIZACIONES[]</strong> - Ver todas mis cotizaciones</li>");
        help.append("<li><strong>VERCOTIZACION[\"id\"]</strong> - Ver detalles de una cotización</li>");
        help.append("</ul>");
        
        help.append("<h3 style='color: #e74c3c;'>MIS COMPRAS</h3>");
        help.append("<ul>");
        help.append("<li><strong>MISCOMPRAS[]</strong> - Ver todas mis compras</li>");
        help.append("<li><strong>MISCOMPRASPENDIENTES[]</strong> - Ver compras con saldo pendiente</li>");
        help.append("<li><strong>VERCOMPRA[\"id\"]</strong> - Ver detalles de una compra</li>");
        help.append("<li><strong>MISALDO[]</strong> - Ver mi saldo total pendiente</li>");
        help.append("</ul>");
        
        help.append("<h3 style='color: #e74c3c;'>MIS PAGOS</h3>");
        help.append("<ul>");
        help.append("<li><strong>MISPAGOS[]</strong> - Ver todos mis pagos realizados</li>");
        help.append("<li><strong>PAGOSDECOMPRA[\"venta_id\"]</strong> - Ver pagos de una compra específica</li>");
        help.append("</ul>");
        
        help.append("<h3 style='color: #e74c3c;'>MIS DEVOLUCIONES</h3>");
        help.append("<ul>");
        help.append("<li><strong>MISDEVOLUCIONES[]</strong> - Ver todas mis devoluciones</li>");
        help.append("<li><strong>VERDEVOLUCION[\"id\"]</strong> - Ver detalles de una devolución</li>");
        help.append("</ul>");
        
        help.append("<p style='margin-top: 20px; color: #7f8c8d;'><em>Nota: Los parámetros entre comillas dobles son obligatorios. Los corchetes [] indican sin parámetros.</em></p>");
        help.append("</div>");
        
        return ResponseFormatter.success("Ayuda del Sistema", help.toString());
    }
}
