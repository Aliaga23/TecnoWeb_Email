package bo.tecnoweb.emailsystem.processor;

import bo.tecnoweb.emailsystem.parser.CommandParser.Command;
import bo.tecnoweb.emailsystem.service.*;
import bo.tecnoweb.emailsystem.model.*;
import bo.tecnoweb.emailsystem.util.ResponseFormatter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class AdminCommandProcessor {
    
    private RolService rolService;
    private UsuarioService usuarioService;
    private CategoriaService categoriaService;
    private ProductoService productoService;
    private ProveedorService proveedorService;
    private DevolucionProveedorService devolucionProveedorService;
    private VentaService ventaService;
    
    public AdminCommandProcessor() {
        this.rolService = new RolService();
        this.usuarioService = new UsuarioService();
        this.categoriaService = new CategoriaService();
        this.productoService = new ProductoService();
        this.proveedorService = new ProveedorService();
        this.devolucionProveedorService = new DevolucionProveedorService();
        this.ventaService = new VentaService();
    }
    
    public String procesarComando(Command comando, String emailUsuario) {
        try {
            switch (comando.getNombre()) {
                // AYUDA
                case "HELP":
                    return mostrarHelp();
                    
                // ROLES
                case "INSERTARROL":
                    return insertarRol(comando);
                case "LISTARROLES":
                    return listarRoles();
                    
                // USUARIOS
                case "INSERTARUSUARIO":
                    return insertarUsuario(comando);
                case "MODIFICARUSUARIO":
                    return modificarUsuario(comando);
                case "ELIMINARUSUARIO":
                    return eliminarUsuario(comando);
                case "LISTARUSUARIOS":
                    return listarUsuarios(comando);
                case "BUSCARUSUARIO":
                    return buscarUsuario(comando);
                    
                // CATEGORIAS
                case "INSERTARCATEGORIA":
                    return insertarCategoria(comando);
                case "LISTARCATEGORIAS":
                    return listarCategorias();
                    
                // PRODUCTOS
                case "INSERTARPRODUCTO":
                    return insertarProducto(comando);
                case "ACTUALIZARSTOCK":
                    return actualizarStock(comando);
                case "ELIMINARPRODUCTO":
                    return eliminarProducto(comando);
                case "LISTARPRODUCTOS":
                    return listarProductos(comando);
                case "BUSCARPRODUCTO":
                    return buscarProducto(comando);
                    
                // PROVEEDORES
                case "INSERTARPROVEEDOR":
                    return insertarProveedor(comando);
                case "ELIMINARPROVEEDOR":
                    return eliminarProveedor(comando);
                case "LISTARPROVEEDORES":
                    return listarProveedores();
                    
                // DEVOLUCIONES A PROVEEDORES
                case "REGISTRARDEVOLUCIONPROVEEDOR":
                    return registrarDevolucionProveedor(comando);
                case "LISTARDEVOLUCIONESPROVEEDOR":
                    return listarDevolucionesProveedor();
                    
                // REPORTES
                case "REPORTEVENTASHOY":
                    return reporteVentasHoy();
                case "REPORTEVENTASMES":
                    return reporteVentasMes();
                case "REPORTEVENTAS":
                    return reporteVentasTotal();
                    
                default:
                    return ResponseFormatter.error("Comando no reconocido: " + comando.getNombre());
            }
        } catch (Exception e) {
            return ResponseFormatter.error("Error al procesar comando: " + e.getMessage());
        }
    }
    
    // ==================== ROLES ====================
    
    private String insertarRol(Command comando) {
        if (comando.getParametros().size() != 1) {
            return ResponseFormatter.error("InsertarRol requiere 1 parametro: nombre\nEjemplo: InsertarRol[\"Administrador\"]");
        }
        
        try {
            String nombre = comando.getParametros().get(0);
            rolService.insertar(nombre);
            return ResponseFormatter.success("Rol Creado", "El rol '" + nombre + "' fue creado exitosamente");
        } catch (SQLException e) {
            return ResponseFormatter.error("Error al crear rol: " + e.getMessage());
        }
    }
    
    private String listarRoles() {
        try {
            List<Rol> roles = rolService.listarTodos();
            
            if (roles.isEmpty()) {
                return ResponseFormatter.success("Lista de Roles", "No hay roles registrados");
            }
            
            StringBuilder tabla = new StringBuilder();
            tabla.append("<table border='1' cellpadding='8' cellspacing='0' style='width:100%; border-collapse: collapse;'>");
            tabla.append("<tr style='background: #27ae60; color: white;'>");
            tabla.append("<th>ID</th><th>Nombre</th>");
            tabla.append("</tr>");
            
            for (Rol rol : roles) {
                tabla.append("<tr>");
                tabla.append("<td>").append(rol.getId()).append("</td>");
                tabla.append("<td>").append(rol.getNombre()).append("</td>");
                tabla.append("</tr>");
            }
            tabla.append("</table>");
            
            return ResponseFormatter.successTable("Lista de Roles", tabla.toString(), roles.size());
        } catch (SQLException e) {
            return ResponseFormatter.error("Error al listar roles: " + e.getMessage());
        }
    }
    
    // ==================== USUARIOS ====================
    
    private String insertarUsuario(Command comando) {
        if (comando.getParametros().size() != 6) {
            return ResponseFormatter.error("InsertarUsuario requiere 6 parametros:\nci, nombre, apellido, telefono, email, rol_nombre\n\nEjemplo:\nInsertarUsuario[\"12345\",\"Juan\",\"Perez\",\"70123456\",\"juan@email.com\",\"administrador\"]");
        }
        
        try {
            String ci = comando.getParametros().get(0);
            String nombre = comando.getParametros().get(1);
            String apellido = comando.getParametros().get(2);
            String telefono = comando.getParametros().get(3);
            String email = comando.getParametros().get(4);
            String rolNombre = comando.getParametros().get(5);
            
            // Buscar el rol por nombre
            Rol rol = rolService.buscarPorNombre(rolNombre);
            if (rol == null) {
                return ResponseFormatter.error("No existe el rol: " + rolNombre);
            }
            
            Usuario usuario = new Usuario();
            usuario.setCi(ci);
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setTelefono(telefono);
            usuario.setCorreo(email);
            usuario.setRolId(rol.getId());
            
            usuarioService.insertar(usuario);
            return ResponseFormatter.success("Usuario Creado", "Usuario '" + nombre + " " + apellido + "' creado exitosamente con rol " + rolNombre);
        } catch (SQLException e) {
            return ResponseFormatter.error("Error al crear usuario: " + e.getMessage());
        }
    }
    
    private String modificarUsuario(Command comando) {
        if (comando.getParametros().size() != 5) {
            return ResponseFormatter.error("ModificarUsuario requiere 5 parametros:\nci, nombre, apellido, telefono, email\n\nEjemplo:\nModificarUsuario[\"12345\",\"Juan\",\"Perez\",\"70123456\",\"juan@email.com\"]");
        }
        
        try {
            String ci = comando.getParametros().get(0);
            String nombre = comando.getParametros().get(1);
            String apellido = comando.getParametros().get(2);
            String telefono = comando.getParametros().get(3);
            String email = comando.getParametros().get(4);
            
            // Buscar el usuario existente
            Usuario usuario = usuarioService.buscarPorCI(ci);
            if (usuario == null) {
                return ResponseFormatter.error("No existe usuario con CI: " + ci);
            }
            
            // Actualizar datos
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setTelefono(telefono);
            usuario.setCorreo(email);
            
            usuarioService.actualizar(usuario);
            return ResponseFormatter.success("Usuario Actualizado", "Usuario '" + nombre + " " + apellido + "' actualizado exitosamente");
        } catch (SQLException e) {
            return ResponseFormatter.error("Error al actualizar usuario: " + e.getMessage());
        }
    }
    
    private String eliminarUsuario(Command comando) {
        if (comando.getParametros().size() != 1) {
            return ResponseFormatter.error("EliminarUsuario requiere 1 parametro: ci");
        }
        
        try {
            String ci = comando.getParametros().get(0);
            usuarioService.eliminar(ci);
            return ResponseFormatter.success("Usuario Eliminado", "Usuario con CI " + ci + " eliminado exitosamente");
        } catch (SQLException e) {
            return ResponseFormatter.error("Error al eliminar usuario: " + e.getMessage());
        }
    }
    
    private String listarUsuarios(Command comando) {
        try {
            List<Usuario> usuarios;
            String titulo = "Lista de Usuarios";
            
            // Si hay parametro, filtrar por rol_nombre
            if (comando.getParametros().size() == 1) {
                String rolNombre = comando.getParametros().get(0);
                Rol rol = rolService.buscarPorNombre(rolNombre);
                
                if (rol == null) {
                    return ResponseFormatter.error("No existe el rol: " + rolNombre);
                }
                
                usuarios = usuarioService.listarPorRol(rol.getId());
                titulo = "Usuarios con rol: " + rolNombre;
            } else if (comando.getParametros().size() == 0) {
                // Sin parametros, listar todos
                usuarios = usuarioService.listarTodos();
            } else {
                return ResponseFormatter.error("ListarUsuarios requiere 0 o 1 parametro\\nListarUsuarios[] - Todos\\nListarUsuarios[\"rol_nombre\"] - Filtrado por rol");
            }
            
            if (usuarios.isEmpty()) {
                return ResponseFormatter.success(titulo, "No hay usuarios registrados");
            }
            
            StringBuilder tabla = new StringBuilder();
            tabla.append("<table border='1' cellpadding='8' cellspacing='0' style='width:100%; border-collapse: collapse;'>");
            tabla.append("<tr style='background: #27ae60; color: white;'>");
            tabla.append("<th>CI</th><th>Nombre</th><th>Apellido</th><th>Telefono</th><th>Email</th><th>Rol ID</th>");
            tabla.append("</tr>");
            
            for (Usuario u : usuarios) {
                tabla.append("<tr>");
                tabla.append("<td>").append(u.getCi()).append("</td>");
                tabla.append("<td>").append(u.getNombre()).append("</td>");
                tabla.append("<td>").append(u.getApellido()).append("</td>");
                tabla.append("<td>").append(u.getTelefono()).append("</td>");
                tabla.append("<td>").append(u.getCorreo()).append("</td>");
                tabla.append("<td>").append(u.getRolId()).append("</td>");
                tabla.append("</tr>");
            }
            tabla.append("</table>");
            
            return ResponseFormatter.successTable(titulo, tabla.toString(), usuarios.size());
        } catch (SQLException e) {
            return ResponseFormatter.error("Error al listar usuarios: " + e.getMessage());
        }
    }
    
    private String buscarUsuario(Command comando) {
        if (comando.getParametros().size() != 1) {
            return ResponseFormatter.error("BuscarUsuario requiere 1 parametro: email\n\nEjemplo:\nBuscarUsuario[\"usuario@email.com\"]");
        }
        
        try {
            String ci = comando.getParametros().get(0);
            Usuario u = usuarioService.buscarPorCI(ci);
            
            if (u == null) {
                return ResponseFormatter.error("No se encontro usuario con CI: " + ci);
            }
            
            String info = "CI: " + u.getCi() + "\n" +
                         "Nombre: " + u.getNombre() + " " + u.getApellido() + "\n" +
                         "Telefono: " + u.getTelefono() + "\n" +
                         "Email: " + u.getCorreo() + "\n" +
                         "Rol ID: " + u.getRolId();
            
            return ResponseFormatter.success("Usuario Encontrado", info);
        } catch (SQLException e) {
            return ResponseFormatter.error("Error al buscar usuario: " + e.getMessage());
        }
    }
    
    // ==================== CATEGORIAS ====================
    
    private String insertarCategoria(Command comando) {
        if (comando.getParametros().size() != 1) {
            return ResponseFormatter.error("InsertarCategoria requiere 1 parametro: nombre\nEjemplo: InsertarCategoria[\"Electronica\"]");
        }
        
        try {
            String nombre = comando.getParametros().get(0);
            categoriaService.insertar(nombre);
            return ResponseFormatter.success("Categoria Creada", "La categoria '" + nombre + "' fue creada exitosamente");
        } catch (SQLException e) {
            return ResponseFormatter.error("Error al crear categoria: " + e.getMessage());
        }
    }
    
    private String listarCategorias() {
        try {
            List<Categoria> categorias = categoriaService.listarTodas();
            
            if (categorias.isEmpty()) {
                return ResponseFormatter.success("Lista de Categorias", "No hay categorias registradas");
            }
            
            StringBuilder tabla = new StringBuilder();
            tabla.append("<table border='1' cellpadding='8' cellspacing='0' style='width:100%; border-collapse: collapse;'>");
            tabla.append("<tr style='background: #27ae60; color: white;'>");
            tabla.append("<th>ID</th><th>Nombre</th>");
            tabla.append("</tr>");
            
            for (Categoria c : categorias) {
                tabla.append("<tr>");
                tabla.append("<td>").append(c.getId()).append("</td>");
                tabla.append("<td>").append(c.getNombre()).append("</td>");
                tabla.append("</tr>");
            }
            tabla.append("</table>");
            
            return ResponseFormatter.successTable("Lista de Categorias", tabla.toString(), categorias.size());
        } catch (SQLException e) {
            return ResponseFormatter.error("Error al listar categorias: " + e.getMessage());
        }
    }
    
    // ==================== PRODUCTOS ====================
    
    private String insertarProducto(Command comando) {
        if (comando.getParametros().size() != 5) {
            return ResponseFormatter.error("InsertarProducto requiere 5 parametros:\nnombre, descripcion, stock, precio, categoria_nombre\n\nEjemplo:\nInsertarProducto[\"Laptop\",\"Laptop HP\",\"10\",\"5000.50\",\"Electronica\"]");
        }
        
        try {
            String nombre = comando.getParametros().get(0);
            String descripcion = comando.getParametros().get(1);
            int stock = Integer.parseInt(comando.getParametros().get(2));
            BigDecimal precio = new BigDecimal(comando.getParametros().get(3));
            String categoriaNombre = comando.getParametros().get(4);
            
            // Buscar categoria por nombre, si no existe crearla
            Categoria categoria = categoriaService.buscarPorNombre(categoriaNombre);
            if (categoria == null) {
                categoriaService.insertar(categoriaNombre);
                categoria = categoriaService.buscarPorNombre(categoriaNombre);
            }
            
            Producto producto = new Producto();
            producto.setNombre(nombre);
            producto.setDescripcion(descripcion);
            producto.setPrecioUnitario(precio);
            producto.setStockActual(stock);
            producto.setCategoriaId(categoria.getId());
            
            productoService.insertar(producto);
            return ResponseFormatter.success("Producto Creado", "Producto '" + nombre + "' creado exitosamente en categoria " + categoriaNombre);
        } catch (SQLException e) {
            return ResponseFormatter.error("Error al crear producto: " + e.getMessage());
        } catch (NumberFormatException e) {
            return ResponseFormatter.error("Stock y precio deben ser numeros validos");
        }
    }
    
    private String actualizarStock(Command comando) {
        if (comando.getParametros().size() != 2) {
            return ResponseFormatter.error("ActualizarStock requiere 2 parametros: idProducto, nuevoStock");
        }
        
        try {
            int id = Integer.parseInt(comando.getParametros().get(0));
            int nuevoStock = Integer.parseInt(comando.getParametros().get(1));
            
            productoService.actualizarStock(id, nuevoStock);
            return ResponseFormatter.success("Stock Actualizado", "Stock del producto ID " + id + " actualizado a " + nuevoStock);
        } catch (SQLException e) {
            return ResponseFormatter.error("Error al actualizar stock: " + e.getMessage());
        } catch (NumberFormatException e) {
            return ResponseFormatter.error("ID y stock deben ser numeros validos");
        }
    }
    
    private String eliminarProducto(Command comando) {
        if (comando.getParametros().size() != 1) {
            return ResponseFormatter.error("EliminarProducto requiere 1 parametro: id");
        }
        
        try {
            int id = Integer.parseInt(comando.getParametros().get(0));
            productoService.eliminar(id);
            return ResponseFormatter.success("Producto Eliminado", "Producto ID " + id + " eliminado exitosamente");
        } catch (SQLException e) {
            return ResponseFormatter.error("Error al eliminar producto: " + e.getMessage());
        } catch (NumberFormatException e) {
            return ResponseFormatter.error("El ID debe ser un numero valido");
        }
    }
    
    private String listarProductos(Command comando) {
        try {
            List<String> params = comando.getParametros();
            List<Producto> productos;
            String titulo;
            
            if (params.isEmpty()) {
                productos = productoService.listarTodos();
                titulo = "Lista de Productos";
            } else {
                String categoriaNombre = params.get(0);
                productos = productoService.listarPorNombreCategoria(categoriaNombre);
                titulo = "Lista de Productos - Categoría: " + categoriaNombre;
            }
            
            if (productos.isEmpty()) {
                return ResponseFormatter.success(titulo, "No hay productos registrados");
            }
            
            StringBuilder tabla = new StringBuilder();
            tabla.append("<table border='1' cellpadding='8' cellspacing='0' style='width:100%; border-collapse: collapse;'>");
            tabla.append("<tr style='background: #27ae60; color: white;'>");
            tabla.append("<th>ID</th><th>Nombre</th><th>Descripcion</th><th>Precio</th><th>Stock</th><th>Categoria</th>");
            tabla.append("</tr>");
            
            for (Producto p : productos) {
                tabla.append("<tr>");
                tabla.append("<td>").append(p.getId()).append("</td>");
                tabla.append("<td>").append(p.getNombre()).append("</td>");
                tabla.append("<td>").append(p.getDescripcion()).append("</td>");
                tabla.append("<td>").append(p.getPrecioUnitario()).append("</td>");
                tabla.append("<td>").append(p.getStockActual()).append("</td>");
                tabla.append("<td>").append(p.getCategoriaId()).append("</td>");
                tabla.append("</tr>");
            }
            tabla.append("</table>");
            
            return ResponseFormatter.successTable(titulo, tabla.toString(), productos.size());
        } catch (SQLException e) {
            return ResponseFormatter.error("Error al listar productos: " + e.getMessage());
        }
    }
    
    private String buscarProducto(Command comando) {
        if (comando.getParametros().size() != 1) {
            return ResponseFormatter.error("BuscarProducto requiere 1 parametro: nombre");
        }
        
        try {
            String nombre = comando.getParametros().get(0);
            Producto producto = productoService.buscarPorNombre(nombre);
            
            if (producto == null) {
                return ResponseFormatter.error("No se encontro producto con el nombre: " + nombre);
            }
            
            String info = "ID: " + producto.getId() + "\n" +
                         "Nombre: " + producto.getNombre() + "\n" +
                         "Descripcion: " + producto.getDescripcion() + "\n" +
                         "Precio: " + producto.getPrecioUnitario() + "\n" +
                         "Stock: " + producto.getStockActual() + "\n" +
                         "Categoria ID: " + producto.getCategoriaId();
            
            return ResponseFormatter.success("Producto Encontrado", info);
        } catch (SQLException e) {
            return ResponseFormatter.error("Error al buscar producto: " + e.getMessage());
        }
    }
    
    // ==================== PROVEEDORES ====================
    
    private String insertarProveedor(Command comando) {
        if (comando.getParametros().size() != 4) {
            return ResponseFormatter.error("InsertarProveedor requiere 4 parametros:\nnombre, telefono, direccion, correo");
        }
        
        try {
            String nombre = comando.getParametros().get(0);
            String telefono = comando.getParametros().get(1);
            String direccion = comando.getParametros().get(2);
            String correo = comando.getParametros().get(3);
            
            Proveedor proveedor = new Proveedor();
            proveedor.setNombre(nombre);
            proveedor.setTelefono(telefono);
            proveedor.setDireccion(direccion);
            proveedor.setCorreo(correo);
            
            proveedorService.insertar(proveedor);
            return ResponseFormatter.success("Proveedor Creado", "Proveedor '" + nombre + "' creado exitosamente");
        } catch (SQLException e) {
            return ResponseFormatter.error("Error al crear proveedor: " + e.getMessage());
        }
    }
    
    private String eliminarProveedor(Command comando) {
        if (comando.getParametros().size() != 1) {
            return ResponseFormatter.error("EliminarProveedor requiere 1 parametro: id");
        }
        
        try {
            int id = Integer.parseInt(comando.getParametros().get(0));
            proveedorService.eliminar(id);
            return ResponseFormatter.success("Proveedor Eliminado", "Proveedor ID " + id + " eliminado exitosamente");
        } catch (SQLException e) {
            return ResponseFormatter.error("Error al eliminar proveedor: " + e.getMessage());
        } catch (NumberFormatException e) {
            return ResponseFormatter.error("El ID debe ser un numero valido");
        }
    }
    
    private String listarProveedores() {
        try {
            List<Proveedor> proveedores = proveedorService.listarTodos();
            
            if (proveedores.isEmpty()) {
                return ResponseFormatter.success("Lista de Proveedores", "No hay proveedores registrados");
            }
            
            StringBuilder tabla = new StringBuilder();
            tabla.append("<table border='1' cellpadding='8' cellspacing='0' style='width:100%; border-collapse: collapse;'>");
            tabla.append("<tr style='background: #27ae60; color: white;'>");
            tabla.append("<th>ID</th><th>Nombre</th><th>Telefono</th><th>Direccion</th><th>Correo</th>");
            tabla.append("</tr>");
            
            for (Proveedor p : proveedores) {
                tabla.append("<tr>");
                tabla.append("<td>").append(p.getId()).append("</td>");
                tabla.append("<td>").append(p.getNombre()).append("</td>");
                tabla.append("<td>").append(p.getTelefono()).append("</td>");
                tabla.append("<td>").append(p.getDireccion()).append("</td>");
                tabla.append("<td>").append(p.getCorreo()).append("</td>");
                tabla.append("</tr>");
            }
            tabla.append("</table>");
            
            return ResponseFormatter.successTable("Lista de Proveedores", tabla.toString(), proveedores.size());
        } catch (SQLException e) {
            return ResponseFormatter.error("Error al listar proveedores: " + e.getMessage());
        }
    }
    
    // ==================== DEVOLUCIONES A PROVEEDORES ====================
    
    private String registrarDevolucionProveedor(Command comando) {
        if (comando.getParametros().size() != 2) {
            return ResponseFormatter.error("RegistrarDevolucionProveedor requiere 2 parametros:\\nproveedor_id, productos\\n\\nEjemplo:\\nRegistrarDevolucionProveedor[\"1\",\"5:10,8:5\"]\\n(producto 5: 10 unidades, producto 8: 5 unidades)");
        }
        
        try {
            int proveedorId = Integer.parseInt(comando.getParametros().get(0));
            String productos = comando.getParametros().get(1);
            
            // Obtener el usuario actual (esto debería venir del contexto, por ahora usamos 1)
            int usuarioId = 1; // TODO: Obtener del usuario autenticado
            
            devolucionProveedorService.registrarDevolucionCompleta(proveedorId, usuarioId, productos);
            
            return ResponseFormatter.success("Devolucion Registrada", 
                "Devolucion al proveedor ID " + proveedorId + " registrada exitosamente.\\nStock actualizado automaticamente.");
        } catch (SQLException e) {
            return ResponseFormatter.error("Error al registrar devolucion: " + e.getMessage());
        } catch (NumberFormatException e) {
            return ResponseFormatter.error("Formato invalido. Use: proveedor_id numerico y productos como \\\"id:cantidad,id:cantidad\\\"");
        }
    }
    
    private String listarDevolucionesProveedor() {
        try {
            List<DevolucionProveedor> devoluciones = devolucionProveedorService.listarTodas();
            
            if (devoluciones.isEmpty()) {
                return ResponseFormatter.success("Lista de Devoluciones a Proveedores", "No hay devoluciones registradas");
            }
            
            StringBuilder tabla = new StringBuilder();
            tabla.append("<table border='1' cellpadding='8' cellspacing='0' style='width:100%; border-collapse: collapse;'>");
            tabla.append("<tr style='background: #27ae60; color: white;'>");
            tabla.append("<th>ID</th><th>Fecha</th><th>Observacion</th><th>Proveedor ID</th><th>Usuario ID</th>");
            tabla.append("</tr>");
            
            for (DevolucionProveedor d : devoluciones) {
                tabla.append("<tr>");
                tabla.append("<td>").append(d.getId()).append("</td>");
                tabla.append("<td>").append(d.getFechaDevolucion()).append("</td>");
                tabla.append("<td>").append(d.getObservacion()).append("</td>");
                tabla.append("<td>").append(d.getProveedorId()).append("</td>");
                tabla.append("<td>").append(d.getUsuarioId()).append("</td>");
                tabla.append("</tr>");
            }
            tabla.append("</table>");
            
            return ResponseFormatter.successTable("Lista de Devoluciones a Proveedores", tabla.toString(), devoluciones.size());
        } catch (SQLException e) {
            return ResponseFormatter.error("Error al listar devoluciones: " + e.getMessage());
        }
    }
    
    // ==================== AYUDA ====================
    
    private String mostrarHelp() {
        StringBuilder help = new StringBuilder();
        help.append("<div style='font-family: Arial, sans-serif;'>");
        help.append("<h2 style='color: #2c3e50;'>COMANDOS DISPONIBLES - ADMINISTRADOR</h2>");
        
        help.append("<h3 style='color: #27ae60;'>GESTIÓN DE ROLES</h3>");
        help.append("<ul>");
        help.append("<li><strong>INSERTARROL[\"nombre\"]</strong> - Crear nuevo rol</li>");
        help.append("<li><strong>LISTARROLES[]</strong> - Listar todos los roles</li>");
        help.append("<li><strong>MODIFICARROL[\"id\",\"nuevo_nombre\"]</strong> - Modificar rol existente</li>");
        help.append("<li><strong>ELIMINARROL[\"id\"]</strong> - Eliminar rol</li>");
        help.append("</ul>");
        
        help.append("<h3 style='color: #27ae60;'>GESTIÓN DE USUARIOS</h3>");
        help.append("<ul>");
        help.append("<li><strong>INSERTARUSUARIO[\"ci\",\"nombre\",\"apellido\",\"telefono\",\"email\",\"rol_nombre\"]</strong> - Crear usuario</li>");
        help.append("<li><strong>MODIFICARUSUARIO[\"ci\",\"nombre\",\"apellido\",\"telefono\",\"email\"]</strong> - Modificar usuario</li>");
        help.append("<li><strong>ELIMINARUSUARIO[\"id\"]</strong> - Eliminar usuario</li>");
        help.append("<li><strong>LISTARUSUARIOS[]</strong> - Listar todos los usuarios</li>");
        help.append("<li><strong>BUSCARUSUARIO[\"email\"]</strong> - Buscar usuario por email</li>");
        help.append("</ul>");
        
        help.append("<h3 style='color: #27ae60;'>GESTIÓN DE CATEGORÍAS</h3>");
        help.append("<ul>");
        help.append("<li><strong>INSERTARCATEGORIA[\"nombre\"]</strong> - Crear categoría</li>");
        help.append("<li><strong>LISTARCATEGORIAS[]</strong> - Listar todas las categorías</li>");
        help.append("<li><strong>MODIFICARCATEGORIA[\"id\",\"nuevo_nombre\"]</strong> - Modificar categoría</li>");
        help.append("<li><strong>ELIMINARCATEGORIA[\"id\"]</strong> - Eliminar categoría</li>");
        help.append("</ul>");
        
        help.append("<h3 style='color: #27ae60;'>GESTIÓN DE PRODUCTOS</h3>");
        help.append("<ul>");
        help.append("<li><strong>INSERTARPRODUCTO[\"nombre\",\"descripcion\",\"stock\",\"precio\",\"categoria\"]</strong> - Crear producto</li>");
        help.append("<li><strong>MODIFICARPRODUCTO[\"id\",\"nombre\",\"descripcion\",\"precio\",\"categoria\"]</strong> - Modificar producto</li>");
        help.append("<li><strong>ACTUALIZARSTOCK[\"id\",\"cantidad\"]</strong> - Actualizar stock de producto</li>");
        help.append("<li><strong>ELIMINARPRODUCTO[\"id\"]</strong> - Eliminar producto</li>");
        help.append("<li><strong>LISTARPRODUCTOS[]</strong> - Listar todos los productos</li>");
        help.append("<li><strong>LISTARPRODUCTOS[\"categoria\"]</strong> - Listar productos por categoría</li>");
        help.append("<li><strong>BUSCARPRODUCTO[\"nombre\"]</strong> - Buscar producto por nombre</li>");
        help.append("</ul>");
        
        help.append("<h3 style='color: #27ae60;'>GESTIÓN DE PROVEEDORES</h3>");
        help.append("<ul>");
        help.append("<li><strong>INSERTARPROVEEDOR[\"nombre\",\"contacto\",\"telefono\",\"email\"]</strong> - Crear proveedor</li>");
        help.append("<li><strong>MODIFICARPROVEEDOR[\"id\",\"nombre\",\"contacto\",\"telefono\",\"email\"]</strong> - Modificar proveedor</li>");
        help.append("<li><strong>ELIMINARPROVEEDOR[\"id\"]</strong> - Eliminar proveedor</li>");
        help.append("<li><strong>LISTARPROVEEDORES[]</strong> - Listar todos los proveedores</li>");
        help.append("<li><strong>BUSCARPROVEEDOR[\"nombre\"]</strong> - Buscar proveedor</li>");
        help.append("</ul>");
        
        help.append("<h3 style='color: #27ae60;'>DEVOLUCIONES A PROVEEDORES</h3>");
        help.append("<ul>");
        help.append("<li><strong>REGISTRARDEVOLUCIONPROVEEDOR[\"proveedor_id\",\"observacion\",\"producto_id\",\"cantidad\",\"motivo\"]</strong> - Registrar devolución</li>");
        help.append("<li><strong>LISTARDEVOLUCIONESPROVEEDOR[]</strong> - Listar devoluciones a proveedores</li>");
        help.append("<li><strong>VERDEVOLUCIONPROVEEDOR[\"id\"]</strong> - Ver detalles de devolución</li>");
        help.append("</ul>");
        
        help.append("<h3 style='color: #27ae60;'>REPORTES</h3>");
        help.append("<ul>");
        help.append("<li><strong>REPORTEVENTASHOY[]</strong> - Reporte de ventas del día actual</li>");
        help.append("<li><strong>REPORTEVENTASMES[]</strong> - Reporte de ventas del mes actual</li>");
        help.append("<li><strong>REPORTEVENTAS[]</strong> - Reporte total de ventas</li>");
        help.append("</ul>");
        
        help.append("<p style='margin-top: 20px; color: #7f8c8d;'><em>Nota: Los parámetros entre comillas dobles son obligatorios. Los corchetes [] indican sin parámetros.</em></p>");
        help.append("</div>");
        
        return ResponseFormatter.success("Ayuda del Sistema", help.toString());
    }
    
    // ==================== REPORTES ====================
    
    private String reporteVentasHoy() {
        try {
            java.sql.Date hoy = new java.sql.Date(System.currentTimeMillis());
            List<Venta> ventas = ventaService.listarTodas();
            
            // Filtrar ventas del día actual
            List<Venta> ventasHoy = new java.util.ArrayList<>();
            for (Venta venta : ventas) {
                if (venta.getFechaVenta().toString().equals(hoy.toString())) {
                    ventasHoy.add(venta);
                }
            }
            
            if (ventasHoy.isEmpty()) {
                return ResponseFormatter.success("Reporte de Ventas - Hoy", "No hay ventas registradas hoy");
            }
            
            double totalVentas = 0;
            double totalPagado = 0;
            double totalPendiente = 0;
            int cantidadVentas = ventasHoy.size();
            
            for (Venta venta : ventasHoy) {
                totalVentas += venta.getTotal().doubleValue();
                if (venta.getEstado().equals("pagada")) {
                    totalPagado += venta.getTotal().doubleValue();
                } else {
                    totalPendiente += venta.getTotal().doubleValue();
                }
            }
            
            StringBuilder html = new StringBuilder();
            html.append("<h3>Reporte de Ventas - ").append(hoy).append("</h3>");
            html.append("<div style='background: #e3f2fd; padding: 15px; margin: 10px 0; border-radius: 5px;'>");
            html.append("<p><strong>Total de ventas:</strong> ").append(cantidadVentas).append("</p>");
            html.append("<p><strong>Monto total:</strong> Bs ").append(String.format("%.2f", totalVentas)).append("</p>");
            html.append("<p><strong>Total pagado:</strong> Bs ").append(String.format("%.2f", totalPagado)).append("</p>");
            html.append("<p><strong>Total pendiente:</strong> Bs ").append(String.format("%.2f", totalPendiente)).append("</p>");
            html.append("</div>");
            
            html.append("<table border='1' cellpadding='8' cellspacing='0' style='width:100%; border-collapse: collapse;'>");
            html.append("<tr style='background: #2196F3; color: white;'>");
            html.append("<th>ID</th><th>Cliente ID</th><th>Vendedor ID</th><th>Total</th><th>Estado</th></tr>");
            
            for (Venta venta : ventasHoy) {
                html.append("<tr>");
                html.append("<td>").append(venta.getId()).append("</td>");
                html.append("<td>").append(venta.getClienteId()).append("</td>");
                html.append("<td>").append(venta.getVendedorId()).append("</td>");
                html.append("<td>Bs ").append(String.format("%.2f", venta.getTotal())).append("</td>");
                html.append("<td style='color: ").append(venta.getEstado().equals("pagada") ? "green" : "orange").append("'>")
                     .append(venta.getEstado().toUpperCase()).append("</td>");
                html.append("</tr>");
            }
            html.append("</table>");
            
            return ResponseFormatter.success("Reporte de Ventas - Hoy", html.toString());
        } catch (Exception e) {
            return ResponseFormatter.error("Error al generar reporte: " + e.getMessage());
        }
    }
    
    private String reporteVentasMes() {
        try {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            int mesActual = cal.get(java.util.Calendar.MONTH);
            int anioActual = cal.get(java.util.Calendar.YEAR);
            
            List<Venta> ventas = ventaService.listarTodas();
            
            // Filtrar ventas del mes actual
            List<Venta> ventasMes = new java.util.ArrayList<>();
            for (Venta venta : ventas) {
                cal.setTime(venta.getFechaVenta());
                if (cal.get(java.util.Calendar.MONTH) == mesActual && 
                    cal.get(java.util.Calendar.YEAR) == anioActual) {
                    ventasMes.add(venta);
                }
            }
            
            if (ventasMes.isEmpty()) {
                return ResponseFormatter.success("Reporte de Ventas - Mes Actual", "No hay ventas registradas este mes");
            }
            
            double totalVentas = 0;
            double totalPagado = 0;
            double totalPendiente = 0;
            int cantidadVentas = ventasMes.size();
            
            for (Venta venta : ventasMes) {
                totalVentas += venta.getTotal().doubleValue();
                if (venta.getEstado().equals("pagada")) {
                    totalPagado += venta.getTotal().doubleValue();
                } else {
                    totalPendiente += venta.getTotal().doubleValue();
                }
            }
            
            String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", 
                            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
            
            StringBuilder html = new StringBuilder();
            html.append("<h3>Reporte de Ventas - ").append(meses[mesActual]).append(" ").append(anioActual).append("</h3>");
            html.append("<div style='background: #e8f5e9; padding: 15px; margin: 10px 0; border-radius: 5px;'>");
            html.append("<p><strong>Total de ventas:</strong> ").append(cantidadVentas).append("</p>");
            html.append("<p><strong>Monto total:</strong> Bs ").append(String.format("%.2f", totalVentas)).append("</p>");
            html.append("<p><strong>Total pagado:</strong> Bs ").append(String.format("%.2f", totalPagado)).append("</p>");
            html.append("<p><strong>Total pendiente:</strong> Bs ").append(String.format("%.2f", totalPendiente)).append("</p>");
            html.append("</div>");
            
            html.append("<table border='1' cellpadding='8' cellspacing='0' style='width:100%; border-collapse: collapse;'>");
            html.append("<tr style='background: #4CAF50; color: white;'>");
            html.append("<th>ID</th><th>Fecha</th><th>Cliente ID</th><th>Vendedor ID</th><th>Total</th><th>Estado</th></tr>");
            
            for (Venta venta : ventasMes) {
                html.append("<tr>");
                html.append("<td>").append(venta.getId()).append("</td>");
                html.append("<td>").append(venta.getFechaVenta()).append("</td>");
                html.append("<td>").append(venta.getClienteId()).append("</td>");
                html.append("<td>").append(venta.getVendedorId()).append("</td>");
                html.append("<td>Bs ").append(String.format("%.2f", venta.getTotal())).append("</td>");
                html.append("<td style='color: ").append(venta.getEstado().equals("pagada") ? "green" : "orange").append("'>")
                     .append(venta.getEstado().toUpperCase()).append("</td>");
                html.append("</tr>");
            }
            html.append("</table>");
            
            return ResponseFormatter.success("Reporte de Ventas - " + meses[mesActual] + " " + anioActual, html.toString());
        } catch (Exception e) {
            return ResponseFormatter.error("Error al generar reporte: " + e.getMessage());
        }
    }
    
    private String reporteVentasTotal() {
        try {
            List<Venta> ventas = ventaService.listarTodas();
            
            if (ventas.isEmpty()) {
                return ResponseFormatter.success("Reporte Total de Ventas", "No hay ventas registradas");
            }
            
            double totalVentas = 0;
            double totalPagado = 0;
            double totalPendiente = 0;
            int cantidadVentas = ventas.size();
            int ventasPagadas = 0;
            int ventasPendientes = 0;
            
            for (Venta venta : ventas) {
                totalVentas += venta.getTotal().doubleValue();
                if (venta.getEstado().equals("pagada")) {
                    totalPagado += venta.getTotal().doubleValue();
                    ventasPagadas++;
                } else {
                    totalPendiente += venta.getTotal().doubleValue();
                    ventasPendientes++;
                }
            }
            
            StringBuilder html = new StringBuilder();
            html.append("<h3>Reporte Total de Ventas</h3>");
            html.append("<div style='background: #fff3e0; padding: 15px; margin: 10px 0; border-radius: 5px;'>");
            html.append("<p><strong>Total de ventas:</strong> ").append(cantidadVentas).append("</p>");
            html.append("<p><strong>Ventas pagadas:</strong> ").append(ventasPagadas).append("</p>");
            html.append("<p><strong>Ventas pendientes:</strong> ").append(ventasPendientes).append("</p>");
            html.append("<p style='font-size: 18px; color: #1976d2;'><strong>Monto total:</strong> Bs ").append(String.format("%.2f", totalVentas)).append("</p>");
            html.append("<p style='color: green;'><strong>Total pagado:</strong> Bs ").append(String.format("%.2f", totalPagado)).append("</p>");
            html.append("<p style='color: orange;'><strong>Total pendiente:</strong> Bs ").append(String.format("%.2f", totalPendiente)).append("</p>");
            html.append("</div>");
            
            html.append("<table border='1' cellpadding='8' cellspacing='0' style='width:100%; border-collapse: collapse;'>");
            html.append("<tr style='background: #FF9800; color: white;'>");
            html.append("<th>ID</th><th>Fecha</th><th>Cliente ID</th><th>Vendedor ID</th><th>Total</th><th>Estado</th></tr>");
            
            for (Venta venta : ventas) {
                html.append("<tr>");
                html.append("<td>").append(venta.getId()).append("</td>");
                html.append("<td>").append(venta.getFechaVenta()).append("</td>");
                html.append("<td>").append(venta.getClienteId()).append("</td>");
                html.append("<td>").append(venta.getVendedorId()).append("</td>");
                html.append("<td>Bs ").append(String.format("%.2f", venta.getTotal())).append("</td>");
                html.append("<td style='color: ").append(venta.getEstado().equals("pagada") ? "green" : "orange").append("'>")
                     .append(venta.getEstado().toUpperCase()).append("</td>");
                html.append("</tr>");
            }
            html.append("</table>");
            
            return ResponseFormatter.success("Reporte Total de Ventas", html.toString());
        } catch (Exception e) {
            return ResponseFormatter.error("Error al generar reporte: " + e.getMessage());
        }
    }
}
