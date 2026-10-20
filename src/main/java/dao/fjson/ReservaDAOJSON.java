package dao.fjson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import dao.ReservaDAO;
import modelo.Espacio;
import modelo.EstadoReserva;
import modelo.Profesor;
import modelo.Reserva;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAOJSON implements ReservaDAO {
    private final Path ruta;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ReservaDAOJSON() {
        this(Path.of("json", "reserva.json"));
    }

    public ReservaDAOJSON(Path ruta) {
        this.ruta = ruta;
    }

    @Override
    public ArrayList<Reserva> leerFichero() throws IOException {
        ArrayList<Reserva> reservas = new ArrayList<>();
        if (!Files.exists(ruta)) return reservas;
        try (Reader reader = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            List<ReservaJSON> datos = gson.fromJson(reader, new TypeToken<List<ReservaJSON>>() {}.getType());
            if (datos == null) return reservas;
            for (ReservaJSON r : datos) {
                Profesor profesor = r.idProfesor == null ? null : new Profesor(r.idProfesor, null, null, null);
                Espacio espacio = r.idEspacio == null ? null : new Espacio(r.idEspacio, null, null, 0, false);
                reservas.add(new Reserva(r.idReserva, LocalDate.parse(r.fecha), LocalTime.parse(r.horaInicio),
                        LocalTime.parse(r.horaFin), profesor, espacio, r.numAsistentes,
                        EstadoReserva.valueOf(r.estado.toUpperCase()), r.motivo));
            }
        } catch (JsonParseException | DateTimeParseException | IllegalArgumentException | NullPointerException e) {
            throw new IOException("Formato JSON incorrecto en " + ruta + ": " + e.getMessage(), e);
        }
        return reservas;
    }

    @Override
    public void escribirFichero(ArrayList<Reserva> reservas) throws IOException {
        List<ReservaJSON> datos = new ArrayList<>();
        for (Reserva r : reservas) {
            datos.add(new ReservaJSON(
                    r.getId(),
                    r.getFecha().toString(),
                    r.getHoraInicio().toString(),
                    r.getHoraFin().toString(),
                    r.getEstado().name(),
                    r.getProfesor() == null ? null : r.getProfesor().getId(),
                    r.getEspacio() == null ? null : r.getEspacio().getId(),
                    r.getMotivo(),
                    r.getNumAsistentes()));
        }
        if (ruta.getParent() != null) Files.createDirectories(ruta.getParent());
        try (Writer writer = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8)) {
            gson.toJson(datos, writer);
        }
    }

    private static class ReservaJSON {
        String idReserva;
        String fecha;
        String horaInicio;
        String horaFin;
        String estado;
        String idProfesor;
        String idEspacio;
        String motivo;
        int numAsistentes;

        ReservaJSON(String idReserva, String fecha, String horaInicio, String horaFin, String estado,
                    String idProfesor, String idEspacio, String motivo, int numAsistentes) {
            this.idReserva = idReserva;
            this.fecha = fecha;
            this.horaInicio = horaInicio;
            this.horaFin = horaFin;
            this.estado = estado;
            this.idProfesor = idProfesor;
            this.idEspacio = idEspacio;
            this.motivo = motivo;
            this.numAsistentes = numAsistentes;
        }
    }
}
