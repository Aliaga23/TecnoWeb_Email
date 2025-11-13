package bo.tecnoweb.emailsystem.processor;

import bo.tecnoweb.emailsystem.model.Usuario;
import bo.tecnoweb.emailsystem.parser.CommandParser;
import bo.tecnoweb.emailsystem.service.UsuarioService;
import bo.tecnoweb.emailsystem.util.ResponseFormatter;


public class CommandProcessor {
    
    private final UsuarioService usuarioService;
    private final AdminCommandProcessor adminProcessor;
    private final VendedorCommandProcessor vendedorProcessor;
    private final ClienteCommandProcessor clienteProcessor;
    
    public CommandProcessor() {
        this.usuarioService = new UsuarioService();
        this.adminProcessor = new AdminCommandProcessor();
        this.vendedorProcessor = new VendedorCommandProcessor();
        this.clienteProcessor = new ClienteCommandProcessor();
    }
    
    public String processCommand(String asunto, String remitente) {
        try {
            System.out.println("\n========================================");
            System.out.println("Procesando comando de: " + remitente);
            System.out.println("Asunto: " + asunto);
            System.out.println("========================================");
            
            // Parsear el comando
            CommandParser.Command cmd = CommandParser.parsear(asunto);
            
            // Verificar si el formato es válido
            if (!cmd.isValido()) {
                return ResponseFormatter.error(cmd.getError());
            }
           
            // Buscar usuario por correo
            Usuario usuario = usuarioService.buscarPorCorreo(remitente);
            
            // Validar que el usuario esté registrado
            if (usuario == null) {
                return ResponseFormatter.error("Usuario no registrado\n\nEl correo " + remitente + " no esta registrado en el sistema.\n\nContacte al administrador.");
            }
            
            // Obtener el rol del usuario
            int rolId = usuario.getRolId();
            
            // Validar que el comando sea válido para el rol
            String comando = cmd.getNombre();
            boolean comandoValido = false;
            
            switch (rolId) {
                case 1: // Administrador
                    comandoValido = CommandParser.validarComandoAdmin(comando);
                    break;
                case 2: // Vendedor
                    comandoValido = CommandParser.validarComandoVendedor(comando);
                    break;
                case 3: // Cliente
                    comandoValido = CommandParser.validarComandoCliente(comando);
                    break;
                default:
                    return ResponseFormatter.error("Rol no reconocido\n\nSu rol no está configurado correctamente.");
            }
            
            if (!comandoValido) {
                return ResponseFormatter.error("Comando no autorizado\n\nEl comando '" + comando + "' no está disponible para su rol.");
            }
            
            // Delegar al processor correspondiente según el rol
            switch (rolId) {
                case 1: // Administrador
                    return adminProcessor.procesarComando(cmd, remitente);
                case 2: // Vendedor
                    return vendedorProcessor.procesarComando(comando, cmd.getParametros(), usuario);
                case 3: // Cliente
                    return clienteProcessor.procesarComando(comando, cmd.getParametros(), usuario);
                default:
                    return ResponseFormatter.error("Sin permisos\n\nSu rol no tiene permisos para ejecutar comandos.");
            }
            
        } catch (Exception e) {
            System.err.println("Error al procesar mensaje: " + e.getMessage());
            e.printStackTrace();
            return ResponseFormatter.error("Error del sistema\n\n" + e.getMessage());
        }
    }
}
