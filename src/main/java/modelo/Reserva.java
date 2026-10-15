package modelo;

import java.sql.Date;

public class Reserva {
    private String id;
    private Date fecha;
    private String horaInicio;
    private String horaFin;
    private Profesor profesor;
    private Espacio espacio;
    private int NumAsistentes;
    private EstadoReserva estado;

    public Reserva(String id, Date fecha, String horaInicio, String horaFin, Profesor profesor, Espacio espacio, int numAsistentes, EstadoReserva estado) {
        this.id = id;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.profesor = profesor;
        this.espacio = espacio;
        NumAsistentes = numAsistentes;
        this.estado = estado;
    }

    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
    public Date getFecha() {return fecha;}
    public void setFecha(Date fecha) {this.fecha = fecha;}
    public String getHoraInicio() {return horaInicio;}
    public void setHoraInicio(String horaInicio) {this.horaInicio = horaInicio;}
    public String getHoraFin() {return horaFin;}
    public void setHoraFin(String horaFin) {this.horaFin = horaFin;}
    public Profesor getProfesor() {return profesor;}
    public void setProfesor(Profesor profesor) {this.profesor = profesor;}
    public Espacio getEspacio() {return espacio;}
    public void setEspacio(Espacio espacio) {this.espacio = espacio;}
    public int getNumAsistentes() {return NumAsistentes;}
    public void setNumAsistentes(int numAsistentes) {NumAsistentes = numAsistentes;}
    public EstadoReserva getEstado() {return estado;}
    public void setEstado(EstadoReserva estado) {this.estado = estado;}
}
