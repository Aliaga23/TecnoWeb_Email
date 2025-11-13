package bo.tecnoweb.emailsystem.service;

import bo.tecnoweb.emailsystem.config.Config;
import java.io.*;
import java.net.Socket;

public class SMTPClient {
    
    private String server;
    private int port;
    private String fromEmail;
    
    public SMTPClient() {
        this.server = Config.getEmailHost();
        this.port = Integer.parseInt(Config.getSmtpPort());
        this.fromEmail = Config.getEmailUser();
    }
    
    public void sendEmail(String to, String subject, String body) throws Exception {
        try (Socket socket = new Socket(server, port);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             DataOutputStream writer = new DataOutputStream(socket.getOutputStream())) {
            
            System.out.println("Conectado a servidor SMTP: " + server + ":" + port);
            
            // Leer saludo del servidor
            String response = reader.readLine();
            System.out.println("S: " + response);
            
            // Enviar comandos SMTP
            sendCommand(writer, reader, "HELO " + server + "\r\n");
            sendCommand(writer, reader, "MAIL FROM: <" + fromEmail + ">\r\n");
            sendCommand(writer, reader, "RCPT TO: <" + to + ">\r\n");
            sendCommand(writer, reader, "DATA\r\n");
            
            // Enviar contenido del email
            String emailContent = "From: " + fromEmail + "\r\n" +
                                "To: " + to + "\r\n" +
                                "Subject: " + subject + "\r\n" +
                                "MIME-Version: 1.0\r\n" +
                                "Content-Type: text/html; charset=UTF-8\r\n" +
                                "\r\n" +
                                body.replace("\n", "\r\n") + "\r\n.\r\n";
            
            writer.writeBytes(emailContent);
            response = readResponse(reader);
            System.out.println("S: " + response.trim());
            
            // Cerrar conexión
            sendCommand(writer, reader, "QUIT\r\n");
            
            System.out.println("✓ Email enviado exitosamente a: " + to);
            
        } catch (Exception e) {
            System.err.println("✗ Error al enviar email: " + e.getMessage());
            throw e;
        }
    }
    
    private void sendCommand(DataOutputStream output, BufferedReader input, String command) throws IOException {
        output.writeBytes(command);
        String response = readResponse(input);
        int code = Integer.parseInt(response.substring(0, 3));
        
        if (code >= 400) {
            throw new IOException("Error SMTP: " + response);
        }
        
        System.out.println("S: " + response.trim());
    }
    
    private String readResponse(BufferedReader reader) throws IOException {
        StringBuilder lines = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            lines.append(line).append("\n");
            if (line.length() > 3 && line.charAt(3) == ' ') {
                break;
            }
        }
        
        if (line == null) {
            throw new IOException("Servidor cerró la conexión inesperadamente");
        }
        
        return lines.toString();
    }
}
