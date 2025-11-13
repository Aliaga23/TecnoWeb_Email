package bo.tecnoweb.emailsystem.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static Properties properties = new Properties();
    
    static {
        try (InputStream input = Config.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("No se pudo encontrar config.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar configuración", e);
        }
    }
    
    // Email configuration
    public static String getEmailUser() {
        return properties.getProperty("email.user");
    }
    
    public static String getEmailPassword() {
        return properties.getProperty("email.password");
    }
    
    public static String getEmailHost() {
        return properties.getProperty("email.host");
    }
    
    public static String getImapPort() {
        return properties.getProperty("email.imap.port");
    }
    
    public static String getSmtpPort() {
        return properties.getProperty("email.smtp.port");
    }
    
    // Database configuration
    public static String getDbHost() {
        return properties.getProperty("db.host");
    }
    
    public static String getDbPort() {
        return properties.getProperty("db.port");
    }
    
    public static String getDbName() {
        return properties.getProperty("db.name");
    }
    
    public static String getDbUser() {
        return properties.getProperty("db.user");
    }
    
    public static String getDbPassword() {
        return properties.getProperty("db.password");
    }
    
    public static String getDbUrl() {
        return String.format("jdbc:postgresql://%s:%s/%s", 
            getDbHost(), getDbPort(), getDbName());
    }
    
    // System configuration
    public static long getCheckInterval() {
        return Long.parseLong(properties.getProperty("system.check.interval", "30000"));
    }
    
    public static boolean isDebug() {
        return Boolean.parseBoolean(properties.getProperty("system.debug", "false"));
    }
}
