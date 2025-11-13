package bo.tecnoweb.emailsystem.processor;

import bo.tecnoweb.emailsystem.database.DatabaseService;
import bo.tecnoweb.emailsystem.model.Persona;
import bo.tecnoweb.emailsystem.parser.CommandParser;
import bo.tecnoweb.emailsystem.service.EmailService;

import javax.mail.Message;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CommandProcessor {
    
    private DatabaseService dbService;
    private EmailService emailService;
    
    public CommandProcessor() {
        this.dbService = new DatabaseService();
        this.emailService = new EmailService();
    }
    
    public String processCommand(String asunto, String remitente) {
        try {
            System.out.println("\n========================================");
            System.out.println("Procesando email de: " + remitente);
            System.out.println("Asunto: " + asunto);
            System.out.println("========================================");
            
            // Parsear el comando
            CommandParser.Command cmd = CommandParser.parsear(asunto);
            
            if (!cmd.isValido()) {
                return formatErrorResponse(asunto, cmd.getError());
            }
            
            // Validar que el comando existe
            if (!CommandParser.validarComando(cmd.getNombre())) {
                return formatErrorResponse(asunto, 
                    "Comando desconocido: " + cmd.getNombre() + 
                    "\nComandos disponibles: LISPER, INSPER, MODPER, ELIPER");
            }
            
            // Ejecutar el comando correspondiente
            String resultado;
            switch (cmd.getNombre()) {
                case "LISPER":
                    resultado = ejecutarLISPER(cmd);
                    break;
                case "INSPER":
                    resultado = ejecutarINSPER(cmd);
                    break;
                case "MODPER":
                    resultado = ejecutarMODPER(cmd);
                    break;
                case "ELIPER":
                    resultado = ejecutarELIPER(cmd);
                    break;
                default:
                    return formatErrorResponse(asunto, "Comando no implementado");
            }
            
            // Verificar si hubo error
            if (resultado.startsWith("ERROR:") || resultado.contains("Parámetros incorrectos")) {
                return formatErrorResponse(asunto, resultado);
            }
            
            return formatSuccessResponse(asunto, resultado);
            
        } catch (Exception e) {
            System.err.println("Error al procesar mensaje: " + e.getMessage());
            e.printStackTrace();
            return formatErrorResponse(asunto, "Error interno: " + e.getMessage());
        }
    }
    
    private String formatErrorResponse(String comando, String error) {
        String fecha = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date());
        return "<!DOCTYPE html>" +
            "<html><head><meta charset='UTF-8'><style>" +
            "body{font-family:Arial,sans-serif;background:#f5f5f5;padding:20px;margin:0}" +
            ".container{max-width:600px;margin:0 auto;background:white;border-radius:8px;box-shadow:0 2px 10px rgba(0,0,0,0.1)}" +
            ".header{background:#dc3545;color:white;padding:20px;border-radius:8px 8px 0 0}" +
            ".content{padding:20px}" +
            ".error-box{background:#f8d7da;border:1px solid #f5c6cb;color:#721c24;padding:15px;border-radius:5px;margin:15px 0}" +
            "table{width:100%;border-collapse:collapse;margin:15px 0}" +
            "td{padding:10px;border-bottom:1px solid #dee2e6}" +
            "td:first-child{font-weight:bold;color:#495057;width:120px}" +
            ".footer{background:#f8f9fa;padding:15px;text-align:center;color:#6c757d;border-radius:0 0 8px 8px;font-size:12px}" +
            "</style></head><body>" +
            "<div class='container'>" +
            "<div class='header'><h2 style='margin:0'>Error en Operación</h2></div>" +
            "<div class='content'>" +
            "<table>" +
            "<tr><td>Comando:</td><td><strong>" + comando + "</strong></td></tr>" +
            "<tr><td>Estado:</td><td><span style='color:#dc3545;font-weight:bold'>ERROR</span></td></tr>" +
            "<tr><td>Fecha:</td><td>" + fecha + "</td></tr>" +
            "</table>" +
            "<div class='error-box'><strong>Detalle del error:</strong><br>" + error + "</div>" +
            "</div>" +
            "<div class='footer'>Sistema de Gestión por Email - Grupo 01SC - TecnoWeb</div>" +
            "</div></body></html>";
    }
    
    private String formatSuccessResponse(String comando, String detalle) {
        String fecha = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date());
        return "<!DOCTYPE html>" +
            "<html><head><meta charset='UTF-8'><style>" +
            "body{font-family:Arial,sans-serif;background:#f5f5f5;padding:20px;margin:0}" +
            ".container{max-width:600px;margin:0 auto;background:white;border-radius:8px;box-shadow:0 2px 10px rgba(0,0,0,0.1)}" +
            ".header{background:#28a745;color:white;padding:20px;border-radius:8px 8px 0 0}" +
            ".content{padding:20px}" +
            "table{width:100%;border-collapse:collapse;margin:15px 0}" +
            "td{padding:10px;border-bottom:1px solid #dee2e6}" +
            "td:first-child{font-weight:bold;color:#495057;width:120px}" +
            ".footer{background:#f8f9fa;padding:15px;text-align:center;color:#6c757d;border-radius:0 0 8px 8px;font-size:12px}" +
            "</style></head><body>" +
            "<div class='container'>" +
            "<div class='header'><h2 style='margin:0'>Operación Exitosa</h2></div>" +
            "<div class='content'>" +
            "<table>" +
            "<tr><td>Comando:</td><td><strong>" + comando + "</strong></td></tr>" +
            "<tr><td>Estado:</td><td><span style='color:#28a745;font-weight:bold'>ÉXITO</span></td></tr>" +
            "<tr><td>Fecha:</td><td>" + fecha + "</td></tr>" +
            "</table>" +
            detalle +
            "</div>" +
            "<div class='footer'>Sistema de Gestión por Email - Grupo 01SC - TecnoWeb</div>" +
            "</div></body></html>";
    }
    
    public void procesarMensaje(Message mensaje) {
        try {
            String remitente = mensaje.getFrom()[0].toString();
            // Extraer solo el email del remitente
            if (remitente.contains("<")) {
                remitente = remitente.substring(remitente.indexOf("<") + 1, remitente.indexOf(">"));
            }
            
            String asunto = mensaje.getSubject();
            
            System.out.println("\n========================================");
            System.out.println("Procesando email de: " + remitente);
            System.out.println("Asunto: " + asunto);
            System.out.println("========================================");
            
            // Parsear el comando
            CommandParser.Command cmd = CommandParser.parsear(asunto);
            
            if (!cmd.isValido()) {
                emailService.enviarRespuestaError(remitente, asunto, cmd.getError());
                emailService.marcarComoLeido(mensaje);
                return;
            }
            
            // Validar que el comando existe
            if (!CommandParser.validarComando(cmd.getNombre())) {
                emailService.enviarRespuestaError(remitente, asunto, 
                    "Comando desconocido: " + cmd.getNombre() + 
                    "\nComandos disponibles: LISPER, INSPER, MODPER, ELIPER");
                emailService.marcarComoLeido(mensaje);
                return;
            }
            
            // Ejecutar el comando correspondiente
            switch (cmd.getNombre()) {
                case "LISPER":
                    ejecutarLISPER(remitente, cmd);
                    break;
                case "INSPER":
                    ejecutarINSPER(remitente, cmd);
                    break;
                case "MODPER":
                    ejecutarMODPER(remitente, cmd);
                    break;
                case "ELIPER":
                    ejecutarELIPER(remitente, cmd);
                    break;
            }
            
            emailService.marcarComoLeido(mensaje);
            
        } catch (Exception e) {
            System.err.println("Error al procesar mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void ejecutarLISPER(String remitente, CommandParser.Command cmd) {
        String response = ejecutarLISPER(cmd);
        if (response.contains("ERROR")) {
            emailService.enviarRespuestaError(remitente, "LISPER", response);
        } else {
            emailService.enviarRespuestaExito(remitente, "LISPER", response);
        }
    }
    
    private String ejecutarLISPER(CommandParser.Command cmd) {
        try {
            if (!CommandParser.validarParametrosLISPER(cmd.getParametros())) {
                return "Parámetros incorrectos. Use: LISPER[\"*\"] o LISPER[\"filtro\"]";
            }
            
            String filtro = cmd.getParametros().isEmpty() ? "*" : cmd.getParametros().get(0);
            List<Persona> personas = dbService.listarPersonas(filtro);
            
            StringBuilder resultado = new StringBuilder();
            resultado.append("<div style='background:#e7f3ff;border-left:4px solid #0066cc;padding:15px;margin:15px 0;border-radius:5px'>");
            resultado.append("<h3 style='margin:0 0 10px 0;color:#0066cc'>Listado de Personas</h3>");
            resultado.append("<p style='margin:0'><strong>Filtro aplicado:</strong> ").append(filtro.equals("*") ? "Todos los registros" : filtro).append("</p>");
            resultado.append("<p style='margin:5px 0 0 0'><strong>Total encontrados:</strong> ").append(personas.size()).append(" registro(s)</p>");
            resultado.append("</div>");
            
            if (personas.isEmpty()) {
                resultado.append("<div style='text-align:center;padding:30px;color:#6c757d'>");
                resultado.append("<p style='font-size:18px;margin:0;font-weight:bold'>Sin resultados</p>");
                resultado.append("<p>No se encontraron registros con el filtro especificado.</p>");
                resultado.append("</div>");
            } else {
                resultado.append("<table style='width:100%;border-collapse:collapse;margin-top:15px'>");
                resultado.append("<thead><tr style='background:#007bff;color:white'>");
                resultado.append("<th style='padding:12px;text-align:left;border:1px solid #dee2e6'>#</th>");
                resultado.append("<th style='padding:12px;text-align:left;border:1px solid #dee2e6'>CI</th>");
                resultado.append("<th style='padding:12px;text-align:left;border:1px solid #dee2e6'>Nombre</th>");
                resultado.append("<th style='padding:12px;text-align:left;border:1px solid #dee2e6'>Apellidos</th>");
                resultado.append("<th style='padding:12px;text-align:left;border:1px solid #dee2e6'>Tipo</th>");
                resultado.append("<th style='padding:12px;text-align:left;border:1px solid #dee2e6'>Teléfono</th>");
                resultado.append("<th style='padding:12px;text-align:left;border:1px solid #dee2e6'>Celular</th>");
                resultado.append("<th style='padding:12px;text-align:left;border:1px solid #dee2e6'>Email</th>");
                resultado.append("</tr></thead><tbody>");
                
                for (int i = 0; i < personas.size(); i++) {
                    Persona p = personas.get(i);
                    String rowColor = (i % 2 == 0) ? "#f8f9fa" : "white";
                    resultado.append("<tr style='background:").append(rowColor).append("'>");
                    resultado.append("<td style='padding:10px;border:1px solid #dee2e6;font-weight:bold'>").append(i + 1).append("</td>");
                    resultado.append("<td style='padding:10px;border:1px solid #dee2e6'>").append(p.getCi()).append("</td>");
                    resultado.append("<td style='padding:10px;border:1px solid #dee2e6'>").append(p.getNombres()).append("</td>");
                    resultado.append("<td style='padding:10px;border:1px solid #dee2e6'>").append(p.getApellidos()).append("</td>");
                    resultado.append("<td style='padding:10px;border:1px solid #dee2e6'>").append(p.getTipo()).append("</td>");
                    resultado.append("<td style='padding:10px;border:1px solid #dee2e6'>").append(p.getTelefono()).append("</td>");
                    resultado.append("<td style='padding:10px;border:1px solid #dee2e6'>").append(p.getCelular()).append("</td>");
                    resultado.append("<td style='padding:10px;border:1px solid #dee2e6'>").append(p.getEmail()).append("</td>");
                    resultado.append("</tr>");
                }
                resultado.append("</tbody></table>");
            }
            
            System.out.println("✓ LISPER ejecutado correctamente");
            return resultado.toString();
            
        } catch (Exception e) {
            System.err.println("✗ Error en LISPER: " + e.getMessage());
            return "ERROR: " + e.getMessage();
        }
    }
    
    private void ejecutarINSPER(String remitente, CommandParser.Command cmd) {
        String response = ejecutarINSPER(cmd);
        if (response.contains("ERROR")) {
            emailService.enviarRespuestaError(remitente, "INSPER", response);
        } else {
            emailService.enviarRespuestaExito(remitente, "INSPER", response);
        }
    }
    
    private String ejecutarINSPER(CommandParser.Command cmd) {
        try {
            if (!CommandParser.validarParametrosINSPER(cmd.getParametros())) {
                return "Parámetros incorrectos. Use: INSPER[\"ci\",\"nombres\",\"apellidos\",\"tipo\",\"telefono\",\"celular\",\"email\"]";
            }
            
            List<String> params = cmd.getParametros();
            
            if (dbService.existePersona(params.get(0))) {
                return "ERROR: Ya existe una persona con CI: " + params.get(0);
            }
            
            Persona persona = new Persona(
                params.get(0), params.get(1), params.get(2), params.get(3), 
                params.get(4), params.get(5), params.get(6)
            );
            
            dbService.insertarPersona(persona);
            
            StringBuilder resultado = new StringBuilder();
            resultado.append("<div style='background:#d4edda;border-left:4px solid #28a745;padding:15px;margin:15px 0;border-radius:5px'>");
            resultado.append("<h3 style='margin:0 0 10px 0;color:#28a745'>Persona Insertada Exitosamente</h3>");
            resultado.append("</div>");
            resultado.append("<table style='width:100%;border-collapse:collapse;margin-top:15px'>");
            resultado.append("<tr style='background:#f8f9fa'><td style='padding:12px;border:1px solid #dee2e6;font-weight:bold;width:150px'>CI:</td><td style='padding:12px;border:1px solid #dee2e6'>").append(persona.getCi()).append("</td></tr>");
            resultado.append("<tr><td style='padding:12px;border:1px solid #dee2e6;font-weight:bold'>Nombres:</td><td style='padding:12px;border:1px solid #dee2e6'>").append(persona.getNombres()).append("</td></tr>");
            resultado.append("<tr style='background:#f8f9fa'><td style='padding:12px;border:1px solid #dee2e6;font-weight:bold'>Apellidos:</td><td style='padding:12px;border:1px solid #dee2e6'>").append(persona.getApellidos()).append("</td></tr>");
            resultado.append("<tr><td style='padding:12px;border:1px solid #dee2e6;font-weight:bold'>Tipo:</td><td style='padding:12px;border:1px solid #dee2e6'>").append(persona.getTipo()).append("</td></tr>");
            resultado.append("<tr style='background:#f8f9fa'><td style='padding:12px;border:1px solid #dee2e6;font-weight:bold'>Teléfono:</td><td style='padding:12px;border:1px solid #dee2e6'>").append(persona.getTelefono()).append("</td></tr>");
            resultado.append("<tr><td style='padding:12px;border:1px solid #dee2e6;font-weight:bold'>Celular:</td><td style='padding:12px;border:1px solid #dee2e6'>").append(persona.getCelular()).append("</td></tr>");
            resultado.append("<tr style='background:#f8f9fa'><td style='padding:12px;border:1px solid #dee2e6;font-weight:bold'>Email:</td><td style='padding:12px;border:1px solid #dee2e6'>").append(persona.getEmail()).append("</td></tr>");
            resultado.append("</table>");
            
            System.out.println("✓ INSPER ejecutado correctamente");
            return resultado.toString();
            
        } catch (Exception e) {
            System.err.println("✗ Error en INSPER: " + e.getMessage());
            return "ERROR: " + e.getMessage();
        }
    }
    
    private void ejecutarMODPER(String remitente, CommandParser.Command cmd) {
        String response = ejecutarMODPER(cmd);
        if (response.contains("ERROR")) {
            emailService.enviarRespuestaError(remitente, "MODPER", response);
        } else {
            emailService.enviarRespuestaExito(remitente, "MODPER", response);
        }
    }
    
    private String ejecutarMODPER(CommandParser.Command cmd) {
        try {
            if (!CommandParser.validarParametrosMODPER(cmd.getParametros())) {
                return "Parámetros incorrectos. Use: MODPER[\"ci\",\"nombres\",\"apellidos\",\"tipo\",\"telefono\",\"celular\",\"email\"]";
            }
            
            List<String> params = cmd.getParametros();
            String ci = params.get(0);
            
            if (!dbService.existePersona(ci)) {
                return "ERROR: No existe persona con CI: " + ci;
            }
            
            Persona persona = new Persona(
                ci, params.get(1), params.get(2), params.get(3),
                params.get(4), params.get(5), params.get(6)
            );
            
            dbService.modificarPersona(ci, persona);
            
            StringBuilder resultado = new StringBuilder();
            resultado.append("<div style='background:#fff3cd;border-left:4px solid #ffc107;padding:15px;margin:15px 0;border-radius:5px'>");
            resultado.append("<h3 style='margin:0 0 10px 0;color:#856404'>Persona Modificada Exitosamente</h3>");
            resultado.append("</div>");
            resultado.append("<table style='width:100%;border-collapse:collapse;margin-top:15px'>");
            resultado.append("<tr style='background:#f8f9fa'><td style='padding:12px;border:1px solid #dee2e6;font-weight:bold;width:150px'>CI:</td><td style='padding:12px;border:1px solid #dee2e6'>").append(persona.getCi()).append("</td></tr>");
            resultado.append("<tr><td style='padding:12px;border:1px solid #dee2e6;font-weight:bold'>Nombres:</td><td style='padding:12px;border:1px solid #dee2e6'>").append(persona.getNombres()).append("</td></tr>");
            resultado.append("<tr style='background:#f8f9fa'><td style='padding:12px;border:1px solid #dee2e6;font-weight:bold'>Apellidos:</td><td style='padding:12px;border:1px solid #dee2e6'>").append(persona.getApellidos()).append("</td></tr>");
            resultado.append("<tr><td style='padding:12px;border:1px solid #dee2e6;font-weight:bold'>Tipo:</td><td style='padding:12px;border:1px solid #dee2e6'>").append(persona.getTipo()).append("</td></tr>");
            resultado.append("<tr style='background:#f8f9fa'><td style='padding:12px;border:1px solid #dee2e6;font-weight:bold'>Teléfono:</td><td style='padding:12px;border:1px solid #dee2e6'>").append(persona.getTelefono()).append("</td></tr>");
            resultado.append("<tr><td style='padding:12px;border:1px solid #dee2e6;font-weight:bold'>Celular:</td><td style='padding:12px;border:1px solid #dee2e6'>").append(persona.getCelular()).append("</td></tr>");
            resultado.append("<tr style='background:#f8f9fa'><td style='padding:12px;border:1px solid #dee2e6;font-weight:bold'>Email:</td><td style='padding:12px;border:1px solid #dee2e6'>").append(persona.getEmail()).append("</td></tr>");
            resultado.append("</table>");
            
            System.out.println("✓ MODPER ejecutado correctamente");
            return resultado.toString();
            
        } catch (Exception e) {
            System.err.println("✗ Error en MODPER: " + e.getMessage());
            return "ERROR: " + e.getMessage();
        }
    }
    
    private void ejecutarELIPER(String remitente, CommandParser.Command cmd) {
        String response = ejecutarELIPER(cmd);
        if (response.contains("ERROR")) {
            emailService.enviarRespuestaError(remitente, "ELIPER", response);
        } else {
            emailService.enviarRespuestaExito(remitente, "ELIPER", response);
        }
    }
    
    private String ejecutarELIPER(CommandParser.Command cmd) {
        try {
            if (!CommandParser.validarParametrosELIPER(cmd.getParametros())) {
                return "Parámetros incorrectos. Use: ELIPER[\"ci\"]";
            }
            
            String ci = cmd.getParametros().get(0);
            
            if (!dbService.existePersona(ci)) {
                return "ERROR: No existe persona con CI: " + ci;
            }
            
            dbService.eliminarPersona(ci);
            
            StringBuilder resultado = new StringBuilder();
            resultado.append("<div style='background:#f8d7da;border-left:4px solid #dc3545;padding:15px;margin:15px 0;border-radius:5px'>");
            resultado.append("<h3 style='margin:0 0 10px 0;color:#721c24'>Persona Eliminada Exitosamente</h3>");
            resultado.append("<p style='margin:0;font-size:16px'>La persona con CI <strong>").append(ci).append("</strong> ha sido eliminada permanentemente de la base de datos.</p>");
            resultado.append("</div>");
            resultado.append("<div style='background:#fff3cd;border:1px solid #ffc107;padding:12px;border-radius:5px;margin-top:15px'>");
            resultado.append("<p style='margin:0;color:#856404'><strong>Nota:</strong> Esta acción no se puede deshacer.</p>");
            resultado.append("</div>");
            
            System.out.println("✓ ELIPER ejecutado correctamente");
            return resultado.toString();
            
        } catch (Exception e) {
            System.err.println("✗ Error en ELIPER: " + e.getMessage());
            return "ERROR: " + e.getMessage();
        }
    }
}
