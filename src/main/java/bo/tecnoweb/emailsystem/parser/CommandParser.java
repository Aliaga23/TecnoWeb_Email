package bo.tecnoweb.emailsystem.parser;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.internet.MimeUtility;

public class CommandParser {
    
    public static class Command {
        private String nombre;
        private List<String> parametros;
        private boolean valido;
        private String error;
        
        public Command(String nombre, List<String> parametros) {
            this.nombre = nombre;
            this.parametros = parametros;
            this.valido = true;
        }
        
        public Command(String error) {
            this.valido = false;
            this.error = error;
        }
        
        public String getNombre() { return nombre; }
        public List<String> getParametros() { return parametros; }
        public boolean isValido() { return valido; }
        public String getError() { return error; }
    }
    
    public static Command parsear(String asunto) {
        if (asunto == null || asunto.trim().isEmpty()) {
            return new Command("El asunto del correo esta vacio");
        }
        
        // Decodificar asunto MIME (RFC 2047) si está codificado
        try {
            // Eliminar espacios entre partes codificadas consecutivas
            asunto = asunto.replaceAll("\\?=\\s+=\\?", "?==?");
            asunto = MimeUtility.decodeText(asunto);
        } catch (Exception e) {
            // Si falla la decodificación, usar el asunto original
            System.out.println("Advertencia: no se pudo decodificar el asunto MIME: " + e.getMessage());
        }
        
        // Limpiar el asunto
        asunto = asunto.trim()
                       .replace("\u201C", "\"")  // comillas tipográficas izquierda
                       .replace("\u201D", "\"")  // comillas tipográficas derecha
                       .replace("\u2018", "'")   // comilla simple izquierda
                       .replace("\u2019", "'");  // comilla simple derecha
        
        // Pattern: COMANDO["param1","param2",...]  
        Pattern pattern = Pattern.compile("^([A-Za-z]+)\\[(.*)\\]$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(asunto);
        
        if (!matcher.matches()) {
            return new Command("Formato incorrecto. Use: COMANDO[\"param1\",\"param2\"]\nEjemplo: InsertarCategoria[\"Electronica\"]");
        }
        
        String comando = matcher.group(1).toUpperCase();
        String paramsStr = matcher.group(2);
        
        List<String> parametros = new ArrayList<>();
        
        if (!paramsStr.trim().isEmpty()) {
            Pattern paramPattern = Pattern.compile("\"([^\"]*)\"");
            Matcher paramMatcher = paramPattern.matcher(paramsStr);
            
            while (paramMatcher.find()) {
                parametros.add(paramMatcher.group(1));
            }
        }
        
        return new Command(comando, parametros);
    }
    
    // Validar comandos de administrador
    public static boolean validarComandoAdmin(String comando) {
        String[] comandosAdmin = {
            // Ayuda
            "HELP",
            // Roles
            "INSERTARROL", "LISTARROLES", "MODIFICARROL", "ELIMINARROL",
            // Usuarios  
            "INSERTARUSUARIO", "MODIFICARUSUARIO", "ELIMINARUSUARIO", "LISTARUSUARIOS", "BUSCARUSUARIO",
            // Categorías
            "INSERTARCATEGORIA", "LISTARCATEGORIAS", "MODIFICARCATEGORIA", "ELIMINARCATEGORIA",
            // Productos
            "INSERTARPRODUCTO", "MODIFICARPRODUCTO", "ACTUALIZARSTOCK", "ELIMINARPRODUCTO", 
            "LISTARPRODUCTOS", "BUSCARPRODUCTO", "VERSTOCK",
            // Proveedores
            "INSERTARPROVEEDOR", "MODIFICARPROVEEDOR", "ELIMINARPROVEEDOR", "LISTARPROVEEDORES", "BUSCARPROVEEDOR",
            // Devoluciones Proveedor
            "REGISTRARDEVOLUCIONPROVEEDOR", "LISTARDEVOLUCIONESPROVEEDOR", "VERDEVOLUCIONPROVEEDOR"
        };
        
        for (String cmd : comandosAdmin) {
            if (cmd.equals(comando)) {
                return true;
            }
        }
        return false;
    }
    
    // Validar comandos de vendedor
    public static boolean validarComandoVendedor(String comando) {
        String[] comandosVendedor = {
            // Ayuda
            "HELP",
            // Gestión de Clientes
            "REGISTRARCLIENTE", "BUSCARCLIENTE", "LISTARCLIENTES",
            // Productos (Solo consulta)
            "LISTARPRODUCTOS", "BUSCARPRODUCTO", "VERSTOCK",
            // Cotización
            "CREARCOTIZACION", "MISCOTIZACIONES", "VERCOTIZACION",
            // Ventas
            "CREARVENTACONTADO", "CREARVENTACREDITO", "ABONARVENTA",
            "MISVENTAS", "VERVENTA", "LISTARVENTASPENDIENTES",
            // Devoluciones
            "REGISTRARDEVOLUCION", "MISDEVOLUCIONES",
            // Consultas
            "VENTASHOY", "MISCOMISIONESHOY"
        };
        
        for (String cmd : comandosVendedor) {
            if (cmd.equals(comando)) {
                return true;
            }
        }
        return false;
    }
    
    // Validar comandos de cliente
    public static boolean validarComandoCliente(String comando) {
        String[] comandosCliente = {
            // Ayuda
            "HELP",
            // Consultas de Productos
            "CATALOGO", "BUSCARPRODUCTO", "VERPRODUCTO",
            // Mis Cotizaciones
            "MISCOTIZACIONES", "VERCOTIZACION",
            // Mis Compras
            "MISCOMPRAS", "MISCOMPRASPENDIENTES", "VERCOMPRA", "MISALDO",
            // Mis Pagos
            "MISPAGOS", "PAGOSDECOMPRA",
            // Mis Devoluciones
            "MISDEVOLUCIONES", "VERDEVOLUCION"
        };
        
        for (String cmd : comandosCliente) {
            if (cmd.equals(comando)) {
                return true;
            }
        }
        return false;
    }
}
