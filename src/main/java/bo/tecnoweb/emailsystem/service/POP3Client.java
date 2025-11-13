package bo.tecnoweb.emailsystem.service;

import bo.tecnoweb.emailsystem.config.Config;
import bo.tecnoweb.emailsystem.processor.CommandProcessor;
import java.io.*;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class POP3Client {
    
    private String host;
    private int port = 110;
    private String user;
    private String password;
    
    private Socket connection;
    private BufferedReader input;
    private DataOutputStream output;
    
    private CommandProcessor commandProcessor;
    private SMTPClient smtpClient;
    
    public POP3Client(CommandProcessor commandProcessor) {
        this.host = Config.getEmailHost();
        String fullUser = Config.getEmailUser();
        this.user = fullUser.contains("@") ? fullUser.split("@")[0] : fullUser;
        this.password = Config.getEmailPassword();
        this.commandProcessor = commandProcessor;
        this.smtpClient = new SMTPClient();
    }
    
    public void connect() throws Exception {
        connection = new Socket(host, port);
        input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        output = new DataOutputStream(connection.getOutputStream());
        input.readLine(); // Leer saludo del servidor
    }
    
    public void close() throws IOException {
        if (connection != null) connection.close();
        if (input != null) input.close();
        if (output != null) output.close();
    }
    
    public void login() throws IOException {
        sendCommand("USER " + user + "\r\n");
        sendCommand("PASS " + password + "\r\n");
    }
    
    public void checkAndProcessEmails() throws IOException {
        String statResponse = sendCommand("STAT\r\n");
        int messageCount = extractMessageCount(statResponse);
        
        if (messageCount > 0) {
            System.out.println("Emails encontrados: " + messageCount);
            
            for (int i = 1; i <= messageCount; i++) {
                String emailContent = sendCommand("RETR " + i + "\r\n");
                processMessageContent(i, emailContent);
            }
        }
    }
    
    private void processMessageContent(int messageNumber, String emailContent) throws IOException {
        EmailInfo emailInfo = parseEmail(emailContent);
        
        if (emailInfo != null && emailInfo.subject != null && !emailInfo.subject.trim().isEmpty()) {
            System.out.println("\n========================================");
            System.out.println("Procesando comando: " + emailInfo.subject);
            System.out.println("De: " + emailInfo.from);
            System.out.println("========================================");
            
            String response;
            try {
                response = commandProcessor.processCommand(emailInfo.subject, emailInfo.from);
            } catch (Exception e) {
                System.err.println("Error procesando comando: " + e.getMessage());
                response = "Error interno al procesar comando: " + e.getMessage();
            }
            
            // Enviar respuesta
            try {
                smtpClient.sendEmail(emailInfo.from, "RE: " + emailInfo.subject, response);
                System.out.println("✓ Respuesta enviada a: " + emailInfo.from);
            } catch (Exception e) {
                System.err.println("✗ Error enviando respuesta: " + e.getMessage());
            }
        }
        
        // Marcar para eliminar
        sendCommand("DELE " + messageNumber + "\r\n");
    }
    
    // Método completo que hace todo el ciclo: conectar, procesar, desconectar
    public void processEmails() {
        try {
            connect();
            login();
            checkAndProcessEmails();
            sendCommand("QUIT\r\n");
        } catch (Exception e) {
            System.err.println("Error procesando emails: " + e.getMessage());
        } finally {
            try {
                close();
            } catch (IOException e) {
                // Ignorar error al cerrar
            }
        }
    }
    
    private EmailInfo parseEmail(String emailContent) {
        EmailInfo info = new EmailInfo();
        String currentHeader = null;
        StringBuilder headerValue = new StringBuilder();
        String[] lines = emailContent.split("\n");
        
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                if (currentHeader != null) {
                    break;
                }
                continue;
            }
            
            if (line.startsWith("From: ") || line.startsWith("Subject: ")) {
                if (currentHeader != null) {
                    processHeader(info, currentHeader, headerValue.toString().trim());
                }
                
                if (line.startsWith("From: ")) {
                    currentHeader = "From";
                    headerValue = new StringBuilder(line.substring(6));
                } else {
                    currentHeader = "Subject";
                    headerValue = new StringBuilder(line.substring(9));
                }
            } else if (line.startsWith(" ") || line.startsWith("\t")) {
                if (currentHeader != null) {
                    headerValue.append(" ").append(line.trim());
                }
            }
        }
        
        if (currentHeader != null && info.subject == null) {
            processHeader(info, currentHeader, headerValue.toString().trim());
        }
        
        return info;
    }
    
    private void processHeader(EmailInfo info, String headerName, String headerValue) {
        if ("From".equals(headerName)) {
            info.from = extractEmailAddress(headerValue);
        } else if ("Subject".equals(headerName)) {
            info.subject = headerValue;
        }
    }
    
    private String extractEmailAddress(String fromField) {
        Pattern emailPattern = Pattern.compile("<(.+?)>|([\\w.-]+@[\\w.-]+\\.[a-zA-Z]+)");
        Matcher matcher = emailPattern.matcher(fromField);
        if (matcher.find()) {
            return matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
        }
        return fromField.trim();
    }
    
    private int extractMessageCount(String statResponse) {
        Pattern pattern = Pattern.compile("\\+OK (\\d+)");
        Matcher matcher = pattern.matcher(statResponse);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }
    
    private String sendCommand(String command) throws IOException {
        output.writeBytes(command);
        output.flush();
        
        if (command.startsWith("RETR") || command.startsWith("LIST")) {
            return readMultilineResponse();
        }
        
        return input.readLine();
    }
    
    private String readMultilineResponse() throws IOException {
        StringBuilder lines = new StringBuilder();
        lines.append(input.readLine());
        
        while (true) {
            String line = input.readLine();
            if (line == null) {
                throw new IOException("Servidor cerró conexión inesperadamente");
            }
            if (line.equals(".")) {
                break;
            }
            if (line.startsWith(".")) {
                line = line.substring(1);
            }
            lines.append("\n").append(line);
        }
        
        return lines.toString();
    }
    
    private static class EmailInfo {
        String from;
        String subject;
    }
}
