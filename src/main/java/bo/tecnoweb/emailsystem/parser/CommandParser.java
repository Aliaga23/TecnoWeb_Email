package bo.tecnoweb.emailsystem.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            return new Command("El asunto del correo está vacío");
        }
        
        asunto = asunto.trim();
        
        // Pattern: COMANDO["param1","param2",...]
        Pattern pattern = Pattern.compile("^([A-Z]+)\\[(.*)\\]$");
        Matcher matcher = pattern.matcher(asunto);
        
        if (!matcher.matches()) {
            return new Command("Formato de comando inválido. Use: COMANDO[\"param1\",\"param2\",...]");
        }
        
        String comando = matcher.group(1);
        String paramsStr = matcher.group(2);
        
        List<String> parametros = new ArrayList<>();
        
        if (!paramsStr.trim().isEmpty()) {
            // Parse parameters: "param1","param2","param3"
            Pattern paramPattern = Pattern.compile("\"([^\"]*)\"");
            Matcher paramMatcher = paramPattern.matcher(paramsStr);
            
            while (paramMatcher.find()) {
                parametros.add(paramMatcher.group(1));
            }
        }
        
        return new Command(comando, parametros);
    }
    
    public static boolean validarComando(String comando) {
        String[] comandosValidos = {"LISPER", "INSPER", "MODPER", "ELIPER"};
        for (String cmd : comandosValidos) {
            if (cmd.equals(comando)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean validarParametrosLISPER(List<String> params) {
        // LISPER puede tener 0 parámetros (listar todos) o 1 parámetro (filtro)
        return params.size() <= 1;
    }
    
    public static boolean validarParametrosINSPER(List<String> params) {
        // INSPER requiere: CI, Nombres, Apellidos, Tipo, Teléfono, Celular, Email
        return params.size() == 7;
    }
    
    public static boolean validarParametrosMODPER(List<String> params) {
        // MODPER requiere: CI (identificador) + campos a modificar
        // Mínimo 2 parámetros (CI + al menos un campo)
        return params.size() >= 2 && params.size() <= 8;
    }
    
    public static boolean validarParametrosELIPER(List<String> params) {
        // ELIPER requiere: CI
        return params.size() == 1;
    }
}
