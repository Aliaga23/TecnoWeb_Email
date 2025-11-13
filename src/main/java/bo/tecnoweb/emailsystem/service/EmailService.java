package bo.tecnoweb.emailsystem.service;

import bo.tecnoweb.emailsystem.config.Config;
import java.util.*;
import javax.mail.*;
import javax.mail.search.*;

public class EmailService {
    
    public List<Message> leerEmailsNoLeidos() {
        List<Message> mensajes = new ArrayList<>();
        Store store = null;
        Folder inbox = null;
        
        try {
            Properties props = new Properties();
            props.setProperty("mail.store.protocol", "imap");
            props.setProperty("mail.imap.host", Config.getEmailHost());
            props.setProperty("mail.imap.port", Config.getImapPort());
            props.setProperty("mail.imap.ssl.enable", "false");
            props.setProperty("mail.imap.starttls.enable", "false");
            
            Session session = Session.getInstance(props);
            store = session.getStore("imap");
            
            System.out.println("Conectando a: " + Config.getEmailUser());
            store.connect(Config.getEmailHost(), Config.getEmailUser(), Config.getEmailPassword());
            
            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
            
            Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            System.out.println("Emails no leídos encontrados: " + messages.length);
            
            mensajes.addAll(Arrays.asList(messages));
            
        } catch (Exception e) {
            System.err.println("Error al leer emails: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (inbox != null && inbox.isOpen()) inbox.close(false);
                if (store != null && store.isConnected()) store.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return mensajes;
    }
    
    public void marcarComoLeido(Message mensaje) {
        try {
            mensaje.setFlag(Flags.Flag.SEEN, true);
        } catch (MessagingException e) {
            System.err.println("Error al marcar mensaje como leído: " + e.getMessage());
        }
    }
    
    public void enviarEmail(String destinatario, String asunto, String cuerpo) {
        try {
            SMTPClient smtpClient = new SMTPClient();
            smtpClient.sendEmail(destinatario, asunto, cuerpo);
        } catch (Exception e) {
            System.err.println("Error al enviar email: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void enviarRespuestaExito(String destinatario, String comando, String detalle) {
        String asunto = "RE: " + comando + " - ÉXITO";
        String cuerpo = String.format(
            "Operación Exitosa\n\n" +
            "Comando: %s\n" +
            "Estado: ÉXITO\n" +
            "Fecha: %s\n\n" +
            "Detalle:\n%s\n\n" +
            "---\n" +
            "Sistema de Gestión por Email - Grupo 01SC",
            comando, new Date(), detalle
        );
        enviarEmail(destinatario, asunto, cuerpo);
    }
    
    public void enviarRespuestaError(String destinatario, String comando, String error) {
        String asunto = "RE: " + comando + " - ERROR";
        String cuerpo = String.format(
            "Error en Operación\n\n" +
            "Comando: %s\n" +
            "Estado: ERROR\n" +
            "Fecha: %s\n\n" +
            "Detalle del error:\n%s\n\n" +
            "---\n" +
            "Sistema de Gestión por Email - Grupo 01SC",
            comando, new Date(), error
        );
        enviarEmail(destinatario, asunto, cuerpo);
    }
}
