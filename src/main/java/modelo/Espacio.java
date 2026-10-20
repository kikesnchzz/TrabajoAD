package modelo;

import java.io.Serializable;

public class Espacio implements Serializable {
    private String id;
    private String nombre;
    private String tipo;
    private String ubicacion;
    private int capacidad;
    private String equipamiento;
    private boolean disponible;

    public Espacio(String id, String nombre, String tipo, int capacidad, boolean disponible) {
        this(id, nombre, tipo, "", capacidad, "", disponible);
    }

    public Espacio(String id, String nombre, String tipo, String ubicacion, int capacidad, String equipamiento, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.ubicacion = ubicacion;
        this.capacidad = capacidad;
        this.equipamiento = equipamiento;
        this.disponible = disponible;
    }

    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
    public String getIdEspacio() {return id;}
    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}
    public String getTipo() {return tipo;}
    public void setTipo(String tipo) {this.tipo = tipo;}
    public String getUbicacion() {return ubicacion;}
    public void setUbicacion(String ubicacion) {this.ubicacion = ubicacion;}
    public int getCapacidad() {return capacidad;}
    public void setCapacidad(int capacidad) {this.capacidad = capacidad;}
    public String getEquipamiento() {return equipamiento;}
    public void setEquipamiento(String equipamiento) {this.equipamiento = equipamiento;}
    public boolean isDisponible() {return disponible;}
    public void setDisponible(boolean disponible) {this.disponible = disponible;}

    @Override
    public String toString() {
        return id + " - " + nombre + " (" + capacidad + ")";
    }
}
