package servicio;

import dao.DepartamentoDAO;
import dao.EspacioDAO;
import dao.ProfesorDAO;
import dao.ReservaDAO;
import modelo.Departamento;
import modelo.Espacio;
import modelo.EstadoReserva;
import modelo.Profesor;
import modelo.Reserva;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CentroEducativo {
    private final DepartamentoDAO departamentoDAO;
    private final ProfesorDAO profesorDAO;
    private final EspacioDAO espacioDAO;
    private final ReservaDAO reservaDAO;

    private final ArrayList<Departamento> departamentos;
    private final ArrayList<Profesor> profesores;
    private final ArrayList<Espacio> espacios;
    private final ArrayList<Reserva> reservas;

    public CentroEducativo(DepartamentoDAO departamentoDAO, ProfesorDAO profesorDAO, EspacioDAO espacioDAO, ReservaDAO reservaDAO) throws IOException {
        this.departamentoDAO = departamentoDAO;
        this.profesorDAO = profesorDAO;
        this.espacioDAO = espacioDAO;
        this.reservaDAO = reservaDAO;

        departamentos = departamentoDAO.leerFichero();
        profesores = profesorDAO.leerFichero();
        espacios = espacioDAO.leerFichero();
        reservas = reservaDAO.leerFichero();

        enlazarRelaciones();
        actualizarReservasFinalizadas();
    }

    private void enlazarRelaciones() throws IOException {
        for (Profesor p : profesores) {
            if (p.getDepartamento() != null) {
                Departamento d = buscarDepartamento(p.getDepartamento().getId());
                if (d == null) {
                    throw new IOException("El profesor " + p.getId() + " referencia un departamento inexistente: " + p.getDepartamento().getId());
                }
                p.setDepartamento(d);
            }
        }
        for (Reserva r : reservas) {
            Profesor p = r.getProfesor() == null ? null : buscarProfesor(r.getProfesor().getId());
            Espacio e = r.getEspacio() == null ? null : buscarEspacio(r.getEspacio().getId());
            if (p == null) throw new IOException("La reserva " + r.getId() + " referencia un profesor inexistente.");
            if (e == null) throw new IOException("La reserva " + r.getId() + " referencia un espacio inexistente.");
            r.setProfesor(p);
            r.setEspacio(e);
        }
    }

    private void actualizarReservasFinalizadas() {
        LocalDateTime ahora = LocalDateTime.now();
        for (Reserva r : reservas) {
            if (r.getEstado() == EstadoReserva.CONFIRMADA && LocalDateTime.of(r.getFecha(), r.getHoraFin()).isBefore(ahora)) {
                r.setEstado(EstadoReserva.FINALIZADA);
            }
        }
    }

    public void guardarTodo() throws IOException {
        departamentoDAO.escribirFichero(departamentos);
        profesorDAO.escribirFichero(profesores);
        espacioDAO.escribirFichero(espacios);
        reservaDAO.escribirFichero(reservas);
    }

    public ArrayList<Departamento> getDepartamentos() {
        return new ArrayList<>(departamentos);
    }

    public Departamento buscarDepartamento(String id) {
        for (Departamento d : departamentos) {
            if (d.getId().equalsIgnoreCase(id)) return d;
        }
        return null;
    }

    public void altaDepartamento(Departamento departamento) throws IOException {
        validarTexto(departamento.getId(), "El identificador del departamento");
        validarTexto(departamento.getNombre(), "El nombre del departamento");
        if (buscarDepartamento(departamento.getId()) != null) {
            throw new IllegalArgumentException("Ya existe un departamento con el identificador " + departamento.getId() + ".");
        }
        departamentos.add(departamento);
        departamentoDAO.escribirFichero(departamentos);
    }

    public void modificarDepartamento(String id, String nuevoNombre) throws IOException {
        Departamento d = obtenerDepartamento(id);
        validarTexto(nuevoNombre, "El nombre del departamento");
        d.setNombre(nuevoNombre);
        departamentoDAO.escribirFichero(departamentos);
    }

    public void bajaDepartamento(String id) throws IOException {
        Departamento d = obtenerDepartamento(id);
        if (!getProfesoresDepartamento(id).isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar el departamento " + id + " porque tiene profesores asociados.");
        }
        departamentos.remove(d);
        departamentoDAO.escribirFichero(departamentos);
    }

    public ArrayList<Profesor> getProfesores() {
        return new ArrayList<>(profesores);
    }

    public ArrayList<Profesor> getProfesoresDepartamento(String idDepartamento) {
        ArrayList<Profesor> resultado = new ArrayList<>();
        for (Profesor p : profesores) {
            if (p.getDepartamento() != null && p.getDepartamento().getId().equalsIgnoreCase(idDepartamento)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public Profesor buscarProfesor(String id) {
        for (Profesor p : profesores) {
            if (p.getId().equalsIgnoreCase(id)) return p;
        }
        return null;
    }

    public void altaProfesor(Profesor profesor) throws IOException {
        validarTexto(profesor.getId(), "El identificador del profesor");
        validarTexto(profesor.getNombre(), "El nombre del profesor");
        if (buscarProfesor(profesor.getId()) != null) {
            throw new IllegalArgumentException("Ya existe un profesor con el identificador " + profesor.getId() + ".");
        }
        profesor.setDepartamento(validarDepartamentoExistente(profesor.getDepartamento()));
        profesores.add(profesor);
        profesorDAO.escribirFichero(profesores);
    }

    public void modificarProfesor(String id, String nombre, String apellidos, String email, Departamento departamento) throws IOException {
        Profesor p = obtenerProfesor(id);
        validarTexto(nombre, "El nombre del profesor");
        Departamento d = validarDepartamentoExistente(departamento);
        p.setNombre(nombre);
        p.setApellidos(apellidos);
        p.setEmail(email);
        p.setDepartamento(d);
        profesorDAO.escribirFichero(profesores);
    }

    public void bajaProfesor(String id) throws IOException {
        Profesor p = obtenerProfesor(id);
        if (!getReservasProfesor(id).isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar el profesor " + id + " porque tiene reservas asociadas.");
        }
        profesores.remove(p);
        profesorDAO.escribirFichero(profesores);
    }

    public ArrayList<Espacio> getEspacios() {
        return new ArrayList<>(espacios);
    }

    public Espacio buscarEspacio(String id) {
        for (Espacio e : espacios) {
            if (e.getId().equalsIgnoreCase(id)) return e;
        }
        return null;
    }

    public void altaEspacio(Espacio espacio) throws IOException {
        validarTexto(espacio.getId(), "El identificador del espacio");
        validarTexto(espacio.getNombre(), "El nombre del espacio");
        validarCapacidad(espacio.getCapacidad());
        if (buscarEspacio(espacio.getId()) != null) {
            throw new IllegalArgumentException("Ya existe un espacio con el identificador " + espacio.getId() + ".");
        }
        espacios.add(espacio);
        espacioDAO.escribirFichero(espacios);
    }

    public void modificarEspacio(String id, String nombre, String tipo, String ubicacion, int capacidad, String equipamiento) throws IOException {
        Espacio e = obtenerEspacio(id);
        validarTexto(nombre, "El nombre del espacio");
        validarCapacidad(capacidad);
        for (Reserva r : getReservasEspacio(id)) {
            if (r.getEstado() == EstadoReserva.CONFIRMADA && r.getNumAsistentes() > capacidad) {
                throw new IllegalArgumentException("La reserva " + r.getId() + " tiene " + r.getNumAsistentes()
                        + " asistentes y supera la nueva capacidad de " + capacidad + ".");
            }
        }
        e.setNombre(nombre);
        e.setTipo(tipo);
        e.setUbicacion(ubicacion);
        e.setCapacidad(capacidad);
        e.setEquipamiento(equipamiento);
        espacioDAO.escribirFichero(espacios);
    }

    public void modificarDisponibilidadEspacio(String id, boolean disponible) throws IOException {
        Espacio e = obtenerEspacio(id);
        e.setDisponible(disponible);
        espacioDAO.escribirFichero(espacios);
    }

    public void bajaEspacio(String id) throws IOException {
        Espacio e = obtenerEspacio(id);
        if (!getReservasEspacio(id).isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar el espacio " + id + " porque tiene reservas asociadas.");
        }
        espacios.remove(e);
        espacioDAO.escribirFichero(espacios);
    }

    public ArrayList<Reserva> getReservas() {
        return new ArrayList<>(reservas);
    }

    public ArrayList<Reserva> getReservasProfesor(String idProfesor) {
        ArrayList<Reserva> resultado = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getProfesor().getId().equalsIgnoreCase(idProfesor)) resultado.add(r);
        }
        return resultado;
    }

    public ArrayList<Reserva> getReservasEspacio(String idEspacio) {
        ArrayList<Reserva> resultado = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getEspacio().getId().equalsIgnoreCase(idEspacio)) resultado.add(r);
        }
        return resultado;
    }

    public Reserva buscarReserva(String id) {
        for (Reserva r : reservas) {
            if (r.getId().equalsIgnoreCase(id)) return r;
        }
        return null;
    }

    public void crearReserva(Reserva reserva) throws IOException {
        validarTexto(reserva.getId(), "El identificador de la reserva");
        if (buscarReserva(reserva.getId()) != null) {
            throw new IllegalArgumentException("Ya existe una reserva con el identificador " + reserva.getId() + ".");
        }
        if (reserva.getFecha() == null || reserva.getHoraInicio() == null || reserva.getHoraFin() == null) {
            throw new IllegalArgumentException("La fecha y el horario de la reserva son obligatorios.");
        }
        if (!reserva.getHoraFin().isAfter(reserva.getHoraInicio())) {
            throw new IllegalArgumentException("La hora de finalizacion debe ser posterior a la hora de inicio.");
        }
        if (reserva.getProfesor() == null) {
            throw new IllegalArgumentException("La reserva debe tener un profesor.");
        }
        if (reserva.getEspacio() == null) {
            throw new IllegalArgumentException("La reserva debe tener un espacio.");
        }
        Profesor profesor = obtenerProfesor(reserva.getProfesor().getId());
        Espacio espacio = obtenerEspacio(reserva.getEspacio().getId());
        if (!espacio.isDisponible()) {
            throw new IllegalArgumentException("El espacio " + espacio.getNombre() + " no esta disponible.");
        }
        if (reserva.getNumAsistentes() <= 0) {
            throw new IllegalArgumentException("El numero de asistentes debe ser mayor que cero.");
        }
        if (reserva.getNumAsistentes() > espacio.getCapacidad()) {
            throw new IllegalArgumentException("El numero de asistentes (" + reserva.getNumAsistentes()
                    + ") supera la capacidad del espacio (" + espacio.getCapacidad() + ").");
        }
        for (Reserva r : reservas) {
            if (r.getEstado() != EstadoReserva.CONFIRMADA || !r.seSolapaCon(reserva)) continue;
            if (r.getEspacio().getId().equalsIgnoreCase(espacio.getId())) {
                throw new IllegalArgumentException("El espacio ya esta reservado en ese horario (reserva " + r.getId() + ").");
            }
            if (r.getProfesor().getId().equalsIgnoreCase(profesor.getId())) {
                throw new IllegalArgumentException("El profesor ya tiene una reserva en ese horario (reserva " + r.getId() + ").");
            }
        }
        reserva.setProfesor(profesor);
        reserva.setEspacio(espacio);
        if (reserva.getEstado() == null) reserva.setEstado(EstadoReserva.CONFIRMADA);
        reservas.add(reserva);
        reservaDAO.escribirFichero(reservas);
    }

    public void cancelarReserva(String id) throws IOException {
        Reserva r = buscarReserva(id);
        if (r == null) throw new IllegalArgumentException("No existe la reserva " + id + ".");
        if (r.getEstado() == EstadoReserva.CANCELADA) {
            throw new IllegalArgumentException("La reserva " + id + " ya esta cancelada.");
        }
        if (r.getEstado() == EstadoReserva.FINALIZADA) {
            throw new IllegalArgumentException("La reserva " + id + " ya ha finalizado y no se puede cancelar.");
        }
        r.setEstado(EstadoReserva.CANCELADA);
        reservaDAO.escribirFichero(reservas);
    }

    private Departamento obtenerDepartamento(String id) {
        Departamento d = buscarDepartamento(id);
        if (d == null) throw new IllegalArgumentException("No existe el departamento " + id + ".");
        return d;
    }

    private Profesor obtenerProfesor(String id) {
        Profesor p = buscarProfesor(id);
        if (p == null) throw new IllegalArgumentException("No existe el profesor " + id + ".");
        return p;
    }

    private Espacio obtenerEspacio(String id) {
        Espacio e = buscarEspacio(id);
        if (e == null) throw new IllegalArgumentException("No existe el espacio " + id + ".");
        return e;
    }

    private Departamento validarDepartamentoExistente(Departamento departamento) {
        if (departamento == null) {
            throw new IllegalArgumentException("El profesor debe pertenecer a un departamento.");
        }
        Departamento d = buscarDepartamento(departamento.getId());
        if (d == null) {
            throw new IllegalArgumentException("El departamento " + departamento.getId() + " no existe.");
        }
        return d;
    }

    private void validarCapacidad(int capacidad) {
        if (capacidad <= 0) throw new IllegalArgumentException("La capacidad del espacio debe ser mayor que cero.");
    }

    private void validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException(campo + " es obligatorio.");
    }
}
