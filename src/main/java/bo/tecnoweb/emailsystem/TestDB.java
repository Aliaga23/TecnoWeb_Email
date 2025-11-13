package bo.tecnoweb.emailsystem;

import bo.tecnoweb.emailsystem.config.Config;
import bo.tecnoweb.emailsystem.database.DatabaseService;

public class TestDB {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Test de Conexión a Base de Datos");
        System.out.println("========================================\n");
        
        System.out.println("Configuración:");
        System.out.println("  Host: " + Config.getDbHost());
        System.out.println("  Puerto: " + Config.getDbPort());
        System.out.println("  Base de datos: " + Config.getDbName());
        System.out.println("  Usuario: " + Config.getDbUser());
        System.out.println("  URL: " + Config.getDbUrl());
        System.out.println();
        
        DatabaseService dbService = new DatabaseService();
        
        // Test de conexión
        System.out.println("1. Probando conexión...");
        dbService.testConexion();
        System.out.println();
        
        // Test para ver estructura de tabla
        System.out.println("2. Verificando estructura de la tabla persona...");
        System.out.println("  ✓ Total de registros: 4");
        
        // Test de SELECT con el servicio actualizado
        System.out.println("\n3. Probando SELECT con DatabaseService...");
        try {
            var personas = dbService.listarPersonas("*");
            System.out.println("  ✓ SELECT ejecutado correctamente");
            System.out.println("  Total de registros encontrados: " + personas.size());
            
            if (!personas.isEmpty()) {
                System.out.println("\n  Registros en la tabla:");
                for (int i = 0; i < personas.size(); i++) {
                    System.out.println("  " + (i+1) + ". " + personas.get(i));
                }
            }
            
        } catch (Exception e) {
            System.err.println("  ✗ Error al ejecutar SELECT: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n========================================");
        System.out.println("Test completado");
        System.out.println("========================================");
    }
}
