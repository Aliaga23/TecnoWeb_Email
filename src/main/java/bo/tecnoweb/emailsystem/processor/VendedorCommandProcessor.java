package bo.tecnoweb.emailsystem.processor;

import bo.tecnoweb.emailsystem.model.*;
import bo.tecnoweb.emailsystem.service.*;
import bo.tecnoweb.emailsystem.util.ResponseFormatter;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VendedorCommandProcessor {
    
    private ClienteService clienteService;
    private ProductoService productoService;
    private CotizacionService cotizacionService;
    private VentaService ventaService;
    private DevolucionService devolucionService;
    
    public VendedorCommandProcessor() {
        this.clienteService = new ClienteService();
        this.productoService = new ProductoService();
        this.cotizacionService = new CotizacionService();
        this.ventaService = new VentaService();
        this.devolucionService = new DevolucionService();
    }
    
    public String procesarComando(String comando, List<String> parametros, Usuario vendedor) {
        try {
            switch (comando) {
                // ===== AYUDA =====
                case "HELP":
                    return mostrarHelp();
                    
                // ===== GESTIÓN DE CLIENTES =====
                case "REGISTRARCLIENTE":
                    return registrarCliente(parametros);
                    
                case "BUSCARCLIENTE":
                    return buscarCliente(parametros);
                    
                case "LISTARCLIENTES":
                    return listarClientes();
                    
                // ===== CONSULTA DE PRODUCTOS =====
                case "LISTARPRODUCTOS":
                    return listarProductos(parametros);
                    
                case "BUSCARPRODUCTO":
                    return buscarProducto(parametros);
                    
                case "VERSTOCK":
                    return verStock(parametros);
                    
                // ===== COTIZACIONES =====
                case "CREARCOTIZACION":
                    return crearCotizacion(parametros, vendedor);
                    
                case "MISCOTIZACIONES":
                    return misCotizaciones(vendedor);
                    
                case "VERCOTIZACION":
                    return verCotizacion(parametros);
                    
                // ===== VENTAS =====
                case "CREARVENTACONTADO":
                    return crearVentaContado(parametros, vendedor);
                    
                case "CREARVENTACREDITO":
                    return crearVentaCredito(parametros, vendedor);
                    
                case "ABONARVENTA":
                    return abonarVenta(parametros);
                    
                case "MISVENTAS":
                    return misVentas(vendedor);
                    
                case "VERVENTA":
                    return verVenta(parametros);
                    
                case "LISTARVENTASPENDIENTES":
                    return listarVentasPendientes();
                    
                case "VENTASHOY":
                    return ventasHoy(vendedor);
                    
                // ===== DEVOLUCIONES =====
                case "REGISTRARDEVOLUCION":
                    return registrarDevolucion(parametros);
                    
                case "MISDEVOLUCIONES":
                    return misDevoluciones(vendedor);
                    
                default:
                    return ResponseFormatter.error("Comando no reconocido para vendedor: " + comando);
            }
        } catch (Exception e) {
            return ResponseFormatter.error("Error al procesar comando: " + e.getMessage());
        }
    }
    
    // ===== IMPLEMENTACIÓN DE COMANDOS =====
    
    private String registrarCliente(List<String> params) throws SQLException {
        if (params.size() != 5) {
            return ResponseFormatter.error("Parametros incorrectos. Use: REGISTRARCLIENTE[\"nombre\",\"email\",\"password\",\"ci\",\"telefono\"]");
        }
        
        String nombre = params.get(0);
        String email = params.get(1);
        String password = params.get(2);
        String ci = params.get(3);
        String telefono = params.get(4);
        
        clienteService.registrarCliente(nombre, email, password, ci, telefono);
        Usuario cliente = clienteService.buscarPorCorreo(email);
        int clienteId = cliente.getId();
        
        return ResponseFormatter.success("Cliente registrado exitosamente", 
            "ID: " + clienteId + "<br>Nombre: " + nombre + "<br>Email: " + email);
    }
    
    private String buscarCliente(List<String> params) throws SQLException {
        if (params.size() != 1) {
            return ResponseFormatter.error("Parametros incorrectos. Use: BUSCARCLIENTE[\"ci_o_email\"]");
        }
        
        String criterio = params.get(0);
        Usuario cliente = null;
        
        // Intentar buscar por CI primero
        if (criterio.matches("\\d+")) {
            cliente = clienteService.buscarPorCI(criterio);
        }
        
        // Si no encontró, buscar por email
        if (cliente == null) {
            cliente = clienteService.buscarPorCorreo(criterio);
        }
        
        if (cliente == null) {
            return ResponseFormatter.error("No se encontro cliente con: " + criterio);
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>");
        html.append("<tr style='background-color: #4CAF50; color: white;'>");
        html.append("<th>ID</th><th>Nombre</th><th>Email</th><th>CI</th><th>Telefono</th></tr>");
        html.append("<tr>");
        html.append("<td>").append(cliente.getId()).append("</td>");
        html.append("<td>").append(cliente.getNombre()).append("</td>");
        html.append("<td>").append(cliente.getCorreo()).append("</td>");
        html.append("<td>").append(cliente.getCi()).append("</td>");
        html.append("<td>").append(cliente.getTelefono()).append("</td>");
        html.append("</tr></table>");
        
        return ResponseFormatter.success("Cliente encontrado", html.toString());
    }
    
    private String listarClientes() throws SQLException {
        List<Usuario> clientes = clienteService.listarTodos();
        
        if (clientes.isEmpty()) {
            return ResponseFormatter.success("Lista de clientes", "No hay clientes registrados");
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>");
        html.append("<tr style='background-color: #4CAF50; color: white;'>");
        html.append("<th>ID</th><th>Nombre</th><th>Email</th><th>CI</th><th>Telefono</th></tr>");
        
        for (Usuario cliente : clientes) {
            html.append("<tr>");
            html.append("<td>").append(cliente.getId()).append("</td>");
            html.append("<td>").append(cliente.getNombre()).append("</td>");
            html.append("<td>").append(cliente.getCorreo()).append("</td>");
            html.append("<td>").append(cliente.getCi()).append("</td>");
            html.append("<td>").append(cliente.getTelefono()).append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        
        return ResponseFormatter.success("Lista de clientes (" + clientes.size() + ")", html.toString());
    }
    
    private String listarProductos(List<String> params) throws SQLException {
        List<Producto> productos;
        
        if (params.isEmpty()) {
            productos = productoService.listarTodos();
        } else {
            String categoria = params.get(0);
            productos = productoService.listarPorNombreCategoria(categoria);
        }
        
        if (productos.isEmpty()) {
            return ResponseFormatter.success("Catalogo de productos", "No hay productos disponibles");
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>");
        html.append("<tr style='background-color: #2196F3; color: white;'>");
        html.append("<th>ID</th><th>Nombre</th><th>Precio</th><th>Stock</th><th>Categoria</th></tr>");
        
        for (Producto producto : productos) {
            html.append("<tr>");
            html.append("<td>").append(producto.getId()).append("</td>");
            html.append("<td>").append(producto.getNombre()).append("</td>");
            html.append("<td>Bs ").append(String.format("%.2f", producto.getPrecioUnitario())).append("</td>");
            html.append("<td>").append(producto.getStockActual()).append("</td>");
            html.append("<td>").append(producto.getCategoriaId()).append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        
        return ResponseFormatter.success("Catalogo de productos (" + productos.size() + ")", html.toString());
    }
    
    private String buscarProducto(List<String> params) throws SQLException {
        if (params.size() != 1) {
            return ResponseFormatter.error("Parametros incorrectos. Use: BUSCARPRODUCTO[\"nombre\"]");
        }
        
        String nombre = params.get(0);
        Producto producto = productoService.buscarPorNombre(nombre);
        
        if (producto == null) {
            return ResponseFormatter.success("Busqueda de productos", "No se encontraron productos con: " + nombre);
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>");
        html.append("<tr style='background-color: #2196F3; color: white;'>");
        html.append("<th>ID</th><th>Nombre</th><th>Precio</th><th>Stock</th></tr>");
        html.append("<tr>");
        html.append("<td>").append(producto.getId()).append("</td>");
        html.append("<td>").append(producto.getNombre()).append("</td>");
        html.append("<td>Bs ").append(String.format("%.2f", producto.getPrecioUnitario())).append("</td>");
        html.append("<td>").append(producto.getStockActual()).append("</td>");
        html.append("</tr>");
        html.append("</table>");
        
        return ResponseFormatter.success("Resultados de busqueda", html.toString());
    }
    
    private String verStock(List<String> params) throws SQLException {
        if (params.size() != 1) {
            return ResponseFormatter.error("Parametros incorrectos. Use: VERSTOCK[\"id_producto\"]");
        }
        
        int productoId = Integer.parseInt(params.get(0));
        Producto producto = productoService.buscarPorId(productoId);
        
        if (producto == null) {
            return ResponseFormatter.error("No existe producto con ID: " + productoId);
        }
        
        String estado = "<span style='color: green;'>DISPONIBLE</span>";
        if (producto.getStockActual() <= 5) {
            estado = "<span style='color: red;'>STOCK BAJO</span>";
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<h3>").append(producto.getNombre()).append("</h3>");
        html.append("<p><b>Stock actual:</b> ").append(producto.getStockActual()).append(" unidades</p>");
        html.append("<p><b>Estado:</b> ").append(estado).append("</p>");
        html.append("<p><b>Precio:</b> Bs ").append(String.format("%.2f", producto.getPrecioUnitario())).append("</p>");
        
        return ResponseFormatter.success("Informacion de stock", html.toString());
    }
    
    private String crearCotizacion(List<String> params, Usuario vendedor) throws SQLException {
        if (params.size() != 2) {
            return ResponseFormatter.error("Parametros incorrectos. Use: CREARCOTIZACION[\"id_cliente\",\"productos\"]<br>" +
                "Formato productos: \"id:cantidad,id:cantidad\"<br>Ejemplo: \"5:2,8:1\"");
        }
        
        int clienteId = Integer.parseInt(params.get(0));
        String productos = params.get(1);
        
        cotizacionService.crearCotizacionCompleta(clienteId, productos);
        
        // Obtener la ultima cotizacion creada
        List<Cotizacion> cotizaciones = cotizacionService.listarPorCliente(clienteId);
        int cotizacionId = cotizaciones.isEmpty() ? 0 : cotizaciones.get(0).getId();
        
        return ResponseFormatter.success("Cotizacion creada exitosamente", 
            "ID de cotizacion: " + cotizacionId + "<br>Use VERCOTIZACION[\"" + cotizacionId + "\"] para ver detalles");
    }
    
    private String misCotizaciones(Usuario vendedor) throws SQLException {
        List<Cotizacion> cotizaciones = cotizacionService.listarTodas();
        
        if (cotizaciones.isEmpty()) {
            return ResponseFormatter.success("Mis cotizaciones", "No tiene cotizaciones registradas");
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>");
        html.append("<tr style='background-color: #FF9800; color: white;'>");
        html.append("<th>ID</th><th>Fecha</th><th>Cliente ID</th><th>Total</th></tr>");
        
        for (Cotizacion cot : cotizaciones) {
            html.append("<tr>");
            html.append("<td>").append(cot.getId()).append("</td>");
            html.append("<td>").append(cot.getFechaCotizacion()).append("</td>");
            html.append("<td>").append(cot.getClienteId()).append("</td>");
            html.append("<td>Bs ").append(String.format("%.2f", cot.getTotal())).append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        
        return ResponseFormatter.success("Mis cotizaciones (" + cotizaciones.size() + ")", html.toString());
    }
    
    private String verCotizacion(List<String> params) throws SQLException {
        if (params.size() != 1) {
            return ResponseFormatter.error("Parametros incorrectos. Use: VERCOTIZACION[\"id\"]");
        }
        
        int cotizacionId = Integer.parseInt(params.get(0));
        Cotizacion cotizacion = cotizacionService.buscarPorId(cotizacionId);
        
        if (cotizacion == null) {
            return ResponseFormatter.error("No existe cotizacion con ID: " + cotizacionId);
        }
        
        List<DetalleCotizacion> detalles = cotizacionService.listarDetalles(cotizacionId);
        
        StringBuilder html = new StringBuilder();
        html.append("<h3>Cotizacion #").append(cotizacionId).append("</h3>");
        html.append("<p><b>Fecha:</b> ").append(cotizacion.getFechaCotizacion()).append("</p>");
        html.append("<p><b>Cliente ID:</b> ").append(cotizacion.getClienteId()).append("</p>");
        
        html.append("<h4>Detalle de productos:</h4>");
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
        
        return ResponseFormatter.success("Detalle de cotizacion", html.toString());
    }
    
    private String crearVentaContado(List<String> params, Usuario vendedor) throws SQLException {
        if (params.size() != 3) {
            return ResponseFormatter.error("Parametros incorrectos. Use: CREARVENTACONTADO[\"id_cliente\",\"productos\",\"metodo_pago\"]<br>" +
                "Metodos: qr, tarjeta, efectivo<br>Formato productos: \"id:cantidad,id:cantidad\"");
        }
        
        int clienteId = Integer.parseInt(params.get(0));
        String productos = params.get(1);
        String metodoPago = params.get(2);
        int vendedorId = vendedor.getId();
        
        ventaService.crearVentaContado(clienteId, vendedorId, productos, metodoPago);
        List<Venta> ventas = ventaService.listarPorVendedor(vendedorId);
        int ventaId = ventas.get(ventas.size() - 1).getId();
        
        return ResponseFormatter.success("Venta al contado registrada", 
            "ID de venta: " + ventaId + "<br>Estado: PAGADA<br>Use VERVENTA[\"" + ventaId + "\"] para ver detalles");
    }
    
    private String crearVentaCredito(List<String> params, Usuario vendedor) throws SQLException {
        if (params.size() != 2) {
            return ResponseFormatter.error("Parametros incorrectos. Use: CREARVENTACREDITO[\"id_cliente\",\"productos\"]<br>" +
                "Formato productos: \"id:cantidad,id:cantidad\"");
        }
        
        int clienteId = Integer.parseInt(params.get(0));
        String productos = params.get(1);
        int vendedorId = vendedor.getId();
        BigDecimal montoInicial = BigDecimal.ZERO;
        String metodoPago = "pendiente";
        
        ventaService.crearVentaCredito(clienteId, vendedorId, productos, montoInicial, metodoPago);
        List<Venta> ventas = ventaService.listarPorVendedor(vendedorId);
        int ventaId = ventas.get(ventas.size() - 1).getId();
        
        return ResponseFormatter.success("Venta a credito registrada", 
            "ID de venta: " + ventaId + "<br>Estado: PENDIENTE<br>Use ABONARVENTA para registrar pagos");
    }
    
    private String abonarVenta(List<String> params) throws SQLException {
        if (params.size() != 3) {
            return ResponseFormatter.error("Parametros incorrectos. Use: ABONARVENTA[\"id_venta\",\"monto\",\"metodo\"]<br>" +
                "Metodos: qr, tarjeta, efectivo");
        }
        
        int ventaId = Integer.parseInt(params.get(0));
        BigDecimal monto = new BigDecimal(params.get(1));
        String metodo = params.get(2);
        
        ventaService.abonarVenta(ventaId, monto, metodo);
        
        Venta venta = ventaService.buscarPorId(ventaId);
        String estado = venta.getEstado().equals("pagada") ? "PAGADA COMPLETAMENTE" : "PENDIENTE";
        
        return ResponseFormatter.success("Abono registrado exitosamente", 
            "Monto: Bs " + String.format("%.2f", monto) + "<br>Estado: " + estado);
    }
    
    private String misVentas(Usuario vendedor) throws SQLException {
        List<Venta> ventas = ventaService.listarPorVendedor(vendedor.getId());
        
        if (ventas.isEmpty()) {
            return ResponseFormatter.success("Mis ventas", "No tiene ventas registradas");
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>");
        html.append("<tr style='background-color: #4CAF50; color: white;'>");
        html.append("<th>ID</th><th>Fecha</th><th>Tipo</th><th>Total</th><th>Estado</th><th>Cliente ID</th></tr>");
        
        for (Venta venta : ventas) {
            String colorEstado = venta.getEstado().equals("pagada") ? "green" : "orange";
            html.append("<tr>");
            html.append("<td>").append(venta.getId()).append("</td>");
            html.append("<td>").append(venta.getFechaVenta()).append("</td>");
            html.append("<td>").append(venta.getTipo().toUpperCase()).append("</td>");
            html.append("<td>Bs ").append(String.format("%.2f", venta.getTotal())).append("</td>");
            html.append("<td style='color: ").append(colorEstado).append(";'><b>")
                .append(venta.getEstado().toUpperCase()).append("</b></td>");
            html.append("<td>").append(venta.getClienteId()).append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        
        return ResponseFormatter.success("Mis ventas (" + ventas.size() + ")", html.toString());
    }
    
    private String verVenta(List<String> params) throws SQLException {
        if (params.size() != 1) {
            return ResponseFormatter.error("Parametros incorrectos. Use: VERVENTA[\"id\"]");
        }
        
        int ventaId = Integer.parseInt(params.get(0));
        Venta venta = ventaService.buscarPorId(ventaId);
        
        if (venta == null) {
            return ResponseFormatter.error("No existe venta con ID: " + ventaId);
        }
        
        List<DetalleVenta> detalles = ventaService.listarDetalles(ventaId);
        List<Pago> pagos = ventaService.listarPagos(ventaId);
        
        StringBuilder html = new StringBuilder();
        html.append("<h3>Venta #").append(ventaId).append("</h3>");
        html.append("<p><b>Fecha:</b> ").append(venta.getFechaVenta()).append("</p>");
        html.append("<p><b>Tipo:</b> ").append(venta.getTipo().toUpperCase()).append("</p>");
        html.append("<p><b>Estado:</b> ").append(venta.getEstado().toUpperCase()).append("</p>");
        html.append("<p><b>Cliente ID:</b> ").append(venta.getClienteId()).append("</p>");
        
        html.append("<h4>Productos vendidos:</h4>");
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
            html.append("<h4>Historial de pagos:</h4>");
            html.append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>");
            html.append("<tr style='background-color: #2196F3; color: white;'>");
            html.append("<th>Fecha</th><th>Monto</th><th>Metodo</th></tr>");
            
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
        
        return ResponseFormatter.success("Detalle de venta", html.toString());
    }
    
    private String listarVentasPendientes() throws SQLException {
        List<Venta> ventas = ventaService.listarPendientes();
        
        if (ventas.isEmpty()) {
            return ResponseFormatter.success("Ventas pendientes", "No hay ventas pendientes");
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>");
        html.append("<tr style='background-color: #FF9800; color: white;'>");
        html.append("<th>ID</th><th>Fecha</th><th>Cliente ID</th><th>Total</th><th>Vendedor ID</th></tr>");
        
        for (Venta venta : ventas) {
            html.append("<tr>");
            html.append("<td>").append(venta.getId()).append("</td>");
            html.append("<td>").append(venta.getFechaVenta()).append("</td>");
            html.append("<td>").append(venta.getClienteId()).append("</td>");
            html.append("<td>Bs ").append(String.format("%.2f", venta.getTotal())).append("</td>");
            html.append("<td>").append(venta.getVendedorId()).append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        
        return ResponseFormatter.success("Ventas pendientes (" + ventas.size() + ")", html.toString());
    }
    
    private String ventasHoy(Usuario vendedor) throws SQLException {
        java.sql.Date hoy = new java.sql.Date(System.currentTimeMillis());
        List<Venta> ventas = ventaService.listarPorVendedorYFecha(vendedor.getId(), hoy);
        
        if (ventas.isEmpty()) {
            return ResponseFormatter.success("Ventas de hoy", "No tiene ventas registradas hoy");
        }
        
        double totalHoy = 0;
        for (Venta venta : ventas) {
            totalHoy += venta.getTotal().doubleValue();
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<h3>Ventas del dia: ").append(hoy).append("</h3>");
        html.append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>");
        html.append("<tr style='background-color: #4CAF50; color: white;'>");
        html.append("<th>ID</th><th>Hora</th><th>Cliente ID</th><th>Total</th><th>Estado</th></tr>");
        
        for (Venta venta : ventas) {
            html.append("<tr>");
            html.append("<td>").append(venta.getId()).append("</td>");
            html.append("<td>").append(venta.getFechaVenta()).append("</td>");
            html.append("<td>").append(venta.getClienteId()).append("</td>");
            html.append("<td>Bs ").append(String.format("%.2f", venta.getTotal())).append("</td>");
            html.append("<td>").append(venta.getEstado().toUpperCase()).append("</td>");
            html.append("</tr>");
        }
        html.append("<tr style='background-color: #f0f0f0; font-weight: bold;'>");
        html.append("<td colspan='3' align='right'>TOTAL DEL DIA:</td>");
        html.append("<td colspan='2'>Bs ").append(String.format("%.2f", totalHoy)).append("</td>");
        html.append("</tr></table>");
        
        return ResponseFormatter.success("Resumen de ventas del dia (" + ventas.size() + " ventas)", html.toString());
    }
    
    private String registrarDevolucion(List<String> params) throws SQLException {
        if (params.size() != 3) {
            return ResponseFormatter.error("Parametros incorrectos. Use: REGISTRARDEVOLUCION[\"id_venta\",\"productos\",\"motivo\"]<br>" +
                "Formato productos: \"id:cantidad,id:cantidad\"");
        }
        
        int ventaId = Integer.parseInt(params.get(0));
        String productos = params.get(1);
        String motivo = params.get(2);
        
        devolucionService.registrarDevolucionCompleta(ventaId, productos, motivo);
        
        return ResponseFormatter.success("Devolucion registrada exitosamente", 
            "Los productos han sido devueltos al inventario<br>Motivo: " + motivo);
    }
    
    private String misDevoluciones(Usuario vendedor) throws SQLException {
        // Obtener todas las devoluciones y filtrar por vendedor
        List<Venta> misVentas = ventaService.listarPorVendedor(vendedor.getId());
        
        StringBuilder html = new StringBuilder();
        html.append("<table border='1' cellpadding='5' style='border-collapse: collapse;'>");
        html.append("<tr style='background-color: #f44336; color: white;'>");
        html.append("<th>ID Dev</th><th>Fecha</th><th>Venta ID</th><th>Motivo</th></tr>");
        
        int count = 0;
        for (Venta venta : misVentas) {
            List<Devolucion> devoluciones = devolucionService.listarPorVenta(venta.getId());
            for (Devolucion dev : devoluciones) {
                html.append("<tr>");
                html.append("<td>").append(dev.getId()).append("</td>");
                html.append("<td>").append(dev.getFechaDevolucion()).append("</td>");
                html.append("<td>").append(dev.getVentaId()).append("</td>");
                html.append("<td>").append(dev.getMotivo()).append("</td>");
                html.append("</tr>");
                count++;
            }
        }
        html.append("</table>");
        
        if (count == 0) {
            return ResponseFormatter.success("Mis devoluciones", "No tiene devoluciones registradas");
        }
        
        return ResponseFormatter.success("Mis devoluciones (" + count + ")", html.toString());
    }
    
    // ==================== AYUDA ====================
    
    private String mostrarHelp() {
        StringBuilder help = new StringBuilder();
        help.append("<div style='font-family: Arial, sans-serif;'>");
        help.append("<h2 style='color: #2c3e50;'>COMANDOS DISPONIBLES - VENDEDOR</h2>");
        
        help.append("<h3 style='color: #3498db;'>GESTIÓN DE CLIENTES</h3>");
        help.append("<ul>");
        help.append("<li><strong>REGISTRARCLIENTE[\"nombre\",\"apellido\",\"email\",\"telefono\",\"nit\"]</strong> - Registrar nuevo cliente</li>");
        help.append("<li><strong>BUSCARCLIENTE[\"email\"]</strong> - Buscar cliente por email</li>");
        help.append("<li><strong>LISTARCLIENTES[]</strong> - Listar todos los clientes</li>");
        help.append("</ul>");
        
        help.append("<h3 style='color: #3498db;'>CONSULTA DE PRODUCTOS</h3>");
        help.append("<ul>");
        help.append("<li><strong>LISTARPRODUCTOS[]</strong> - Listar todos los productos</li>");
        help.append("<li><strong>LISTARPRODUCTOS[\"categoria\"]</strong> - Listar productos por categoría</li>");
        help.append("<li><strong>BUSCARPRODUCTO[\"nombre\"]</strong> - Buscar producto por nombre</li>");
        help.append("<li><strong>VERSTOCK[\"producto_id\"]</strong> - Ver stock de un producto</li>");
        help.append("</ul>");
        
        help.append("<h3 style='color: #3498db;'>COTIZACIONES</h3>");
        help.append("<ul>");
        help.append("<li><strong>CREARCOTIZACION[\"cliente_email\",\"producto_id\",\"cantidad\",\"producto_id\",\"cantidad\",...]</strong> - Crear cotización</li>");
        help.append("<li><strong>MISCOTIZACIONES[]</strong> - Ver mis cotizaciones</li>");
        help.append("<li><strong>VERCOTIZACION[\"id\"]</strong> - Ver detalles de cotización</li>");
        help.append("</ul>");
        
        help.append("<h3 style='color: #3498db;'>VENTAS</h3>");
        help.append("<ul>");
        help.append("<li><strong>CREARVENTACONTADO[\"cliente_email\",\"producto_id\",\"cantidad\",\"producto_id\",\"cantidad\",...]</strong> - Venta al contado</li>");
        help.append("<li><strong>CREARVENTACREDITO[\"cliente_email\",\"dias_credito\",\"producto_id\",\"cantidad\",...]</strong> - Venta a crédito</li>");
        help.append("<li><strong>ABONARVENTA[\"venta_id\",\"monto\"]</strong> - Registrar abono a venta</li>");
        help.append("<li><strong>MISVENTAS[]</strong> - Ver mis ventas</li>");
        help.append("<li><strong>VERVENTA[\"id\"]</strong> - Ver detalles de venta</li>");
        help.append("<li><strong>LISTARVENTASPENDIENTES[]</strong> - Listar ventas pendientes de pago</li>");
        help.append("</ul>");
        
        help.append("<h3 style='color: #3498db;'>DEVOLUCIONES</h3>");
        help.append("<ul>");
        help.append("<li><strong>REGISTRARDEVOLUCION[\"venta_id\",\"observacion\",\"producto_id\",\"cantidad\",\"motivo\"]</strong> - Registrar devolución</li>");
        help.append("<li><strong>MISDEVOLUCIONES[]</strong> - Ver mis devoluciones</li>");
        help.append("</ul>");
        
        help.append("<h3 style='color: #3498db;'>CONSULTAS</h3>");
        help.append("<ul>");
        help.append("<li><strong>VENTASHOY[]</strong> - Ver total de ventas del día</li>");
        help.append("<li><strong>MISCOMISIONESHOY[]</strong> - Ver comisiones ganadas hoy</li>");
        help.append("</ul>");
        
        help.append("<p style='margin-top: 20px; color: #7f8c8d;'><em>Nota: Los parámetros entre comillas dobles son obligatorios. Los corchetes [] indican sin parámetros.</em></p>");
        help.append("</div>");
        
        return ResponseFormatter.success("Ayuda del Sistema", help.toString());
    }
}
