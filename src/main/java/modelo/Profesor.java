package modelo;

public class Profesor {
    private String id;
    private String nombre;
    private String email;
    private Departamento departamento;

    public Profesor(String id, String nombre, String email, Departamento departamento) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.departamento = departamento;
    }

    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public Departamento getDepartamento() {return departamento;}
    public void setDepartamento(Departamento departamento) {this.departamento = departamento;}
}
