package bo.tecnoweb.emailsystem.processor;

import bo.tecnoweb.emailsystem.model.Usuario;
import bo.tecnoweb.emailsystem.parser.CommandParser;
import bo.tecnoweb.emailsystem.service.UsuarioService;
import bo.tecnoweb.emailsystem.util.ResponseFormatter;


public class CommandProcessor {
    
    private final UsuarioService usuarioService;
    private final AdminCommandProcessor adminProcessor;
    
    public CommandProcessor() {
        this.usuarioService = new UsuarioService();
        this.adminProcessor = new AdminCommandProcessor();
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
            
            // Delegar al processor correspondiente según el rol
            switch (rolId) {
                case 1: // Administrador
                    return adminProcessor.procesarComando(cmd, remitente);
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
