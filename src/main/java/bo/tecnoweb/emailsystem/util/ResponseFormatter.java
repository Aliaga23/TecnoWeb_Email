package bo.tecnoweb.emailsystem.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ResponseFormatter {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    public static String success(String titulo, String contenido) {
        String fecha = DATE_FORMAT.format(new Date());
        
        return "<!DOCTYPE html>\n" +
               "<html>\n" +
               "<head>\n" +
               "    <meta charset='UTF-8'>\n" +
               "    <style>\n" +
               "        body { font-family: Arial, sans-serif; background: #f5f5f5; margin: 0; padding: 20px; }\n" +
               "        .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }\n" +
               "        .header { background: #27ae60; color: white; padding: 20px; text-align: center; }\n" +
               "        .header h1 { margin: 0; font-size: 22px; }\n" +
               "        .fecha { opacity: 0.9; margin-top: 5px; font-size: 13px; }\n" +
               "        .content { padding: 30px; }\n" +
               "        .titulo { color: #27ae60; font-size: 18px; font-weight: bold; margin-bottom: 15px; }\n" +
               "        .info { background: #e8f5e9; border-left: 4px solid #27ae60; padding: 15px; border-radius: 4px; line-height: 1.6; white-space: pre-line; }\n" +
               "        .footer { background: #34495e; color: white; text-align: center; padding: 15px; font-size: 12px; }\n" +
               "    </style>\n" +
               "</head>\n" +
               "<body>\n" +
               "    <div class='container'>\n" +
               "        <div class='header'>\n" +
               "            <h1>SISTEMA DE VENTAS - GRUPO 01SC</h1>\n" +
               "            <div class='fecha'>" + fecha + "</div>\n" +
               "        </div>\n" +
               "        <div class='content'>\n" +
               "            <div class='titulo'>" + titulo + "</div>\n" +
               "            <div class='info'>" + contenido + "</div>\n" +
               "        </div>\n" +
               "        <div class='footer'>Sistema automatizado de gestion por email</div>\n" +
               "    </div>\n" +
               "</body>\n" +
               "</html>";
    }
    
    public static String error(String mensaje) {
        return "<!DOCTYPE html>\n" +
               "<html>\n" +
               "<head>\n" +
               "    <meta charset='UTF-8'>\n" +
               "    <style>\n" +
               "        body { font-family: Arial, sans-serif; background: #f5f5f5; margin: 0; padding: 20px; }\n" +
               "        .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }\n" +
               "        .header { background: #e74c3c; color: white; padding: 20px; text-align: center; }\n" +
               "        .header h1 { margin: 0; font-size: 22px; }\n" +
               "        .content { padding: 30px; }\n" +
               "        .error-box { background: #ffebee; border-left: 4px solid #e74c3c; padding: 20px; border-radius: 4px; }\n" +
               "        .error-message { color: #333; line-height: 1.8; white-space: pre-line; }\n" +
               "        .footer { background: #34495e; color: white; text-align: center; padding: 15px; font-size: 12px; }\n" +
               "    </style>\n" +
               "</head>\n" +
               "<body>\n" +
               "    <div class='container'>\n" +
               "        <div class='header'>\n" +
               "            <h1>SISTEMA DE VENTAS - GRUPO 01SC</h1>\n" +
               "        </div>\n" +
               "        <div class='content'>\n" +
               "            <div class='error-box'>\n" +
               "                <div class='error-message'>" + mensaje + "</div>\n" +
               "            </div>\n" +
               "        </div>\n" +
               "        <div class='footer'>Sistema automatizado de gestion por email</div>\n" +
               "    </div>\n" +
               "</body>\n" +
               "</html>";
    }
    
    public static String successTable(String titulo, String tablaHtml, int totalRegistros) {
        String fecha = DATE_FORMAT.format(new Date());
        
        return "<!DOCTYPE html>\n" +
               "<html>\n" +
               "<head>\n" +
               "    <meta charset='UTF-8'>\n" +
               "    <style>\n" +
               "        body { font-family: Arial, sans-serif; background: #f5f5f5; margin: 0; padding: 20px; }\n" +
               "        .container { max-width: 800px; margin: 0 auto; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }\n" +
               "        .header { background: #27ae60; color: white; padding: 20px; text-align: center; }\n" +
               "        .header h1 { margin: 0; font-size: 22px; }\n" +
               "        .fecha { opacity: 0.9; margin-top: 5px; font-size: 13px; }\n" +
               "        .content { padding: 30px; }\n" +
               "        .titulo { color: #27ae60; font-size: 18px; font-weight: bold; margin-bottom: 15px; }\n" +
               "        .tabla { width: 100%; overflow-x: auto; }\n" +
               "        .total { margin-top: 15px; padding: 10px; background: #e8f5e9; border-radius: 4px; font-weight: bold; }\n" +
               "        .footer { background: #34495e; color: white; text-align: center; padding: 15px; font-size: 12px; }\n" +
               "    </style>\n" +
               "</head>\n" +
               "<body>\n" +
               "    <div class='container'>\n" +
               "        <div class='header'>\n" +
               "            <h1>SISTEMA DE VENTAS - GRUPO 01SC</h1>\n" +
               "            <div class='fecha'>" + fecha + "</div>\n" +
               "        </div>\n" +
               "        <div class='content'>\n" +
               "            <div class='titulo'>" + titulo + "</div>\n" +
               "            <div class='tabla'>" + tablaHtml + "</div>\n" +
               "            <div class='total'>Total de registros: " + totalRegistros + "</div>\n" +
               "        </div>\n" +
               "        <div class='footer'>Sistema automatizado de gestion por email</div>\n" +
               "    </div>\n" +
               "</body>\n" +
               "</html>";
    }
}
