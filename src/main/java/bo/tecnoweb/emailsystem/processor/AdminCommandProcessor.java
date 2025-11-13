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
    
    public AdminCommandProcessor() {
        this.rolService = new RolService();
        this.usuarioService = new UsuarioService();
        this.categoriaService = new CategoriaService();
        this.productoService = new ProductoService();
        this.proveedorService = new ProveedorService();
        this.devolucionProveedorService = new DevolucionProveedorService();
    }
    
    public String procesarComando(Command comando, String emailUsuario) {
        try {
            switch (comando.getNombre()) {
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
            return ResponseFormatter.error("ModificarUsuario requiere 5 parametros:\nci, nombre, apellido, telefono, correo\n\nEjemplo:\nModificarUsuario[\"12345\",\"Juan\",\"Perez\",\"70123456\",\"juan@email.com\"]");
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
            return ResponseFormatter.error("BuscarUsuario requiere 1 parametro: ci");
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
            return ResponseFormatter.error("InsertarProducto requiere 5 parametros:\nnombre, descripcion, stock, precio, categoria_nombre\n\nEjemplo:\nInsertarProducto[\"Laptop\",\"Laptop HP\",\"5000.50\",\"10\",\"Electronica\"]");
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
}
