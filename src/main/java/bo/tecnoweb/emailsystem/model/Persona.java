package bo.tecnoweb.emailsystem.model;

public class Persona {
    private String ci;
    private String nombres;
    private String apellidos;
    private String tipo;
    private String telefono;
    private String celular;
    private String email;
    
    public Persona() {}
    
    public Persona(String ci, String nombres, String apellidos, String tipo, 
                   String telefono, String celular, String email) {
        this.ci = ci;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.tipo = tipo;
        this.telefono = telefono;
        this.celular = celular;
        this.email = email;
    }
    
    // Getters and Setters
    public String getCi() { return ci; }
    public void setCi(String ci) { this.ci = ci; }
    
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    @Override
    public String toString() {
        return String.format("CI: %s | Nombres: %s | Apellidos: %s | Tipo: %s | Tel: %s | Cel: %s | Email: %s",
            ci, nombres, apellidos, tipo, telefono, celular, email);
    }
}
