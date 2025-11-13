package bo.tecnoweb.emailsystem;

import bo.tecnoweb.emailsystem.config.Config;
import bo.tecnoweb.emailsystem.database.DatabaseService;
import bo.tecnoweb.emailsystem.processor.CommandProcessor;
import bo.tecnoweb.emailsystem.service.POP3Client;

public class Main {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Sistema de Comandos por Email");
        System.out.println("  Grupo 01SC - TecnoWeb");
        System.out.println("========================================\n");
        
        // Verificar configuración
        System.out.println("Configuración:");
        System.out.println("  Email: " + Config.getEmailUser());
        System.out.println("  Host: " + Config.getEmailHost());
        System.out.println("  DB: " + Config.getDbName());
        System.out.println();
        
        // Test de conexión a BD
        DatabaseService dbService = new DatabaseService();
        dbService.testConexion();
        System.out.println();
        
        // Iniciar procesamiento automático
        CommandProcessor processor = new CommandProcessor();
        POP3Client pop3 = new POP3Client(processor);
        
        System.out.println("========================================");
        System.out.println("Sistema iniciado en modo automatico");
        System.out.println("Revisando emails cada 5 segundos...");
        System.out.println("Presione Ctrl+C para detener");
        System.out.println("========================================\n");
        
        // Bucle infinito revisando emails - conecta y desconecta en cada ciclo
        while (true) {
            try {
                System.out.println("[" + new java.util.Date() + "] Revisando emails...");
                pop3.processEmails(); // Conecta, procesa, QUIT, cierra
                
                Thread.sleep(5000); // 5 segundos entre revisiones
            } catch (InterruptedException e) {
                System.out.println("\nSistema detenido por el usuario");
                break;
            }
        }
    }
}
