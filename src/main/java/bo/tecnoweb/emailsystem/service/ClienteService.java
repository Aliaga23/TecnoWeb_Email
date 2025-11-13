package bo.tecnoweb.emailsystem.service;

import bo.tecnoweb.emailsystem.model.Usuario;
import bo.tecnoweb.emailsystem.model.Rol;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteService {
    
    private UsuarioService usuarioService;
    private RolService rolService;
    
    public ClienteService() {
        this.usuarioService = new UsuarioService();
        this.rolService = new RolService();
    }
    
    /**
     * Registra un nuevo cliente automáticamente con rol "cliente"
     */
    public void registrarCliente(String ci, String nombre, String apellido, String telefono, String correo) throws SQLException {
        // Buscar el rol "cliente"
        Rol rolCliente = rolService.buscarPorNombre("cliente");
        if (rolCliente == null) {
            throw new SQLException("No existe el rol 'cliente'. Por favor crear primero el rol.");
        }
        
        Usuario cliente = new Usuario();
        cliente.setCi(ci);
        cliente.setNombre(nombre);
        cliente.setApellido(apellido);
        cliente.setTelefono(telefono);
        cliente.setCorreo(correo);
        cliente.setRolId(rolCliente.getId());
        
        usuarioService.insertar(cliente);
    }
    
    /**
     * Busca un cliente por CI
     */
    public Usuario buscarPorCI(String ci) throws SQLException {
        return usuarioService.buscarPorCI(ci);
    }
    
    /**
     * Busca un cliente por correo
     */
    public Usuario buscarPorCorreo(String correo) throws SQLException {
        return usuarioService.buscarPorCorreo(correo);
    }
    
    /**
     * Lista todos los clientes (usuarios con rol "cliente")
     */
    public List<Usuario> listarTodos() throws SQLException {
        // Buscar el rol "cliente"
        Rol rolCliente = rolService.buscarPorNombre("cliente");
        if (rolCliente == null) {
            return new ArrayList<>();
        }
        
        return usuarioService.listarPorRol(rolCliente.getId());
    }
}
