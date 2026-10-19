package modelo;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;

public class Reserva  implements Serializable {
    private String idReserva;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Profesor profesor;
    private Espacio espacio;
    private int NumAsistentes;
    private String motivo;
    private EstadoReserva estado;

    public Reserva() {
    }

    public Reserva(String id, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, Profesor profesor, Espacio espacio, int numAsistentes, EstadoReserva estado, String motivo) {
        this.idReserva = id;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.profesor = profesor;
        this.espacio = espacio;
        NumAsistentes = numAsistentes;
        this.estado = estado;
        this.motivo = motivo;
    }

    public String getId() {return idReserva;}
    public void setId(String id) {this.idReserva = id;}
    public LocalDate getFecha() {return fecha;}
    public void setFecha(LocalDate fecha) {this.fecha = fecha;}
    public LocalTime getHoraInicio() {return horaInicio;}
    public void setHoraInicio(LocalTime horaInicio) {this.horaInicio = horaInicio;}
    public LocalTime getHoraFin() {return horaFin;}
    public void setHoraFin(LocalTime horaFin) {this.horaFin = horaFin;}
    public String getMotivo() {return motivo;}
    public void setMotivo(String motivo) {this.motivo = motivo;}
    public Profesor getProfesor() {return profesor;}
    public void setProfesor(Profesor profesor) {this.profesor = profesor;}
    public Espacio getEspacio() {return espacio;}
    public void setEspacio(Espacio espacio) {this.espacio = espacio;}
    public int getNumAsistentes() {return NumAsistentes;}
    public void setNumAsistentes(int numAsistentes) {NumAsistentes = numAsistentes;}
    public EstadoReserva getEstado() {return estado;}
    public void setEstado(EstadoReserva estado) {this.estado = estado;}
}
