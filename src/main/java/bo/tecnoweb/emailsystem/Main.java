package bo.tecnoweb.emailsystem;

import bo.tecnoweb.emailsystem.config.Config;
import bo.tecnoweb.emailsystem.database.DatabaseService;
import bo.tecnoweb.emailsystem.processor.CommandProcessor;
import bo.tecnoweb.emailsystem.service.EmailService;
import bo.tecnoweb.emailsystem.service.POP3Client;

import javax.mail.Message;
import java.util.List;
import java.util.Scanner;

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
        
        // Iniciar procesamiento
        EmailService emailService = new EmailService();
        CommandProcessor processor = new CommandProcessor();
        
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;
        
        System.out.println("========================================");
        System.out.println("Sistema iniciado. Opciones:");
        System.out.println("  1 - Revisar emails (POP3)");
        System.out.println("  2 - Modo automático (cada 30 segundos)");
        System.out.println("  3 - Enviar email de prueba");
        System.out.println("  0 - Salir");
        System.out.println("========================================\n");
        
        while (continuar) {
            System.out.print("Opción: ");
            String opcion = scanner.nextLine().trim();
            
            switch (opcion) {
                case "1":
                    revisarEmailsPOP3(processor);
                    break;
                    
                case "2":
                    modoAutomatico(emailService, processor, scanner);
                    break;
                    
                case "3":
                    enviarEmailPrueba(emailService, scanner);
                    break;
                    
                case "0":
                    continuar = false;
                    System.out.println("\nSistema finalizado. ¡Hasta pronto!");
                    break;
                    
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
        
        scanner.close();
    }
    
    private static void revisarEmailsPOP3(CommandProcessor processor) {
        System.out.println("\n--- Revisando emails con POP3 ---");
        try {
            POP3Client pop3 = new POP3Client(processor);
            pop3.processEmails();
        } catch (Exception e) {
            System.err.println("Error al revisar emails: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void revisarEmails(EmailService emailService, CommandProcessor processor) {
        System.out.println("\n--- Revisando emails ---");
        try {
            List<Message> mensajes = emailService.leerEmailsNoLeidos();
            
            if (mensajes.isEmpty()) {
                System.out.println("No hay emails nuevos.");
            } else {
                System.out.println("Procesando " + mensajes.size() + " email(s)...\n");
                for (Message mensaje : mensajes) {
                    processor.procesarMensaje(mensaje);
                }
                System.out.println("\nTodos los emails fueron procesados.");
            }
        } catch (Exception e) {
            System.err.println("Error al revisar emails: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }
    
    private static void modoAutomatico(EmailService emailService, CommandProcessor processor, Scanner scanner) {
        System.out.println("\n--- Modo Automático Iniciado ---");
        System.out.println("Revisando emails cada 30 segundos...");
        System.out.println("Presione ENTER para detener\n");
        
        Thread thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    System.out.println("[" + new java.util.Date() + "] Revisando emails...");
                    List<Message> mensajes = emailService.leerEmailsNoLeidos();
                    
                    if (!mensajes.isEmpty()) {
                        System.out.println("  → Encontrados " + mensajes.size() + " email(s) nuevos");
                        for (Message mensaje : mensajes) {
                            processor.procesarMensaje(mensaje);
                        }
                    } else {
                        System.out.println("  → Sin emails nuevos");
                    }
                    
                    Thread.sleep(Config.getCheckInterval());
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    System.err.println("Error en modo automático: " + e.getMessage());
                }
            }
        });
        
        thread.start();
        scanner.nextLine(); // Esperar ENTER
        thread.interrupt();
        
        System.out.println("Modo automático detenido.\n");
    }
    
    private static void enviarEmailPrueba(EmailService emailService, Scanner scanner) {
        System.out.println("\n--- Enviar Email de Prueba ---");
        System.out.print("Destinatario: ");
        String destinatario = scanner.nextLine().trim();
        
        if (destinatario.isEmpty()) {
            System.out.println("Destinatario no puede estar vacío.");
            return;
        }
        
        System.out.print("Asunto: ");
        String asunto = scanner.nextLine().trim();
        
        if (asunto.isEmpty()) {
            asunto = "Prueba - Sistema Email Grupo 01SC";
        }
        
        String cuerpo = "Este es un mensaje de prueba del Sistema de Comandos por Email.\n\n" +
                       "Comandos disponibles:\n" +
                       "- LISPER[\"*\"] - Listar todas las personas\n" +
                       "- INSPER[\"ci\",\"nombres\",\"apellidos\",\"tipo\",\"telefono\",\"celular\",\"email\"] - Insertar persona\n" +
                       "- MODPER[\"ci\",\"nombres\",\"apellidos\",\"tipo\",\"telefono\",\"celular\",\"email\"] - Modificar persona\n" +
                       "- ELIPER[\"ci\"] - Eliminar persona\n\n" +
                       "Ejemplo de uso:\n" +
                       "Asunto: LISPER[\"*\"]\n\n" +
                       "---\n" +
                       "Grupo 01SC - TecnoWeb";
        
        try {
            emailService.enviarEmail(destinatario, asunto, cuerpo);
            System.out.println("Email enviado exitosamente.\n");
        } catch (Exception e) {
            System.err.println("Error al enviar email: " + e.getMessage());
        }
    }
}
