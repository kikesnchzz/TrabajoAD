package modelo;

import java.io.Serializable;

public class Profesor implements Serializable {
    private String id;
    private String nombre;
    private String apellidos;
    private String email;
    private Departamento departamento;

    public Profesor(String id, String nombre, String email, Departamento departamento) {
        this(id, nombre, "", email, departamento);
    }

    public Profesor(String id, String nombre, String apellidos, String email, Departamento departamento) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.departamento = departamento;
    }

    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
    public String getIdProfesor() {return id;}
    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}
    public String getApellidos() {return apellidos;}
    public void setApellidos(String apellidos) {this.apellidos = apellidos;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public Departamento getDepartamento() {return departamento;}
    public void setDepartamento(Departamento departamento) {this.departamento = departamento;}

    public String getNombreCompleto() {
        return apellidos == null || apellidos.isBlank() ? nombre : nombre + " " + apellidos;
    }

    @Override
    public String toString() {
        return id + " - " + getNombreCompleto();
    }
}
