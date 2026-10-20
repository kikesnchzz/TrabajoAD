package modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Reserva implements Serializable {
    private String id;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Profesor profesor;
    private Espacio espacio;
    private int numAsistentes;
    private String motivo;
    private EstadoReserva estado;

    public Reserva(String id, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, Profesor profesor, Espacio espacio, int numAsistentes, EstadoReserva estado) {
        this(id, fecha, horaInicio, horaFin, profesor, espacio, numAsistentes, estado, "");
    }

    public Reserva(String id, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, Profesor profesor, Espacio espacio, int numAsistentes, EstadoReserva estado, String motivo) {
        this.id = id;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.profesor = profesor;
        this.espacio = espacio;
        this.numAsistentes = numAsistentes;
        this.estado = estado;
        this.motivo = motivo;
    }

    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
    public String getIdReserva() {return id;}
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
    public int getNumAsistentes() {return numAsistentes;}
    public void setNumAsistentes(int numAsistentes) {this.numAsistentes = numAsistentes;}
    public EstadoReserva getEstado() {return estado;}
    public void setEstado(EstadoReserva estado) {this.estado = estado;}

    public boolean seSolapaCon(Reserva otra) {
        return fecha.equals(otra.fecha) && horaInicio.isBefore(otra.horaFin) && otra.horaInicio.isBefore(horaFin);
    }
}
