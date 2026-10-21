package dao.fcsv;

import dao.ReservaDAO;
import modelo.Espacio;
import modelo.EstadoReserva;
import modelo.Profesor;
import modelo.Reserva;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAOCSV implements ReservaDAO {
    private final Path ruta;

    public ReservaDAOCSV() {
        this(Path.of("csv", "reserva.csv"));
    }

    public ReservaDAOCSV(Path ruta) {
        this.ruta = ruta;
    }

    @Override
    public ArrayList<Reserva> leerFichero() throws IOException {
        ArrayList<Reserva> reservas = new ArrayList<>();
        List<List<String>> filas = leerFilas();
        for (int i = 0; i < filas.size(); i++) {
            List<String> fila = filas.get(i);
            if (fila.size() != 9) {
                throw errorFormato(i + 1, "se esperaban 9 campos y se encontraron " + fila.size());
            }
            try {
                Profesor profesor = fila.get(6).isEmpty() ? null : new Profesor(fila.get(6), null, null, null);
                Espacio espacio = fila.get(7).isEmpty() ? null : new Espacio(fila.get(7), null, null, 0, false);
                reservas.add(new Reserva(fila.get(0), LocalDate.parse(fila.get(1)), LocalTime.parse(fila.get(2)),
                        LocalTime.parse(fila.get(3)), profesor, espacio, Integer.parseInt(fila.get(8)),
                        EstadoReserva.valueOf(fila.get(4).toUpperCase()), fila.get(5)));
            } catch (DateTimeParseException | IllegalArgumentException e) {
                throw errorFormato(i + 1, e.getMessage());
            }
        }
        return reservas;
    }

    @Override
    public void escribirFichero(ArrayList<Reserva> reservas) throws IOException {
        if (ruta.getParent() != null) Files.createDirectories(ruta.getParent());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ruta.toFile(), StandardCharsets.UTF_8))) {
            for (Reserva reserva : reservas) {
                escribirFila(writer, reserva.getId(), reserva.getFecha().toString(),
                        reserva.getHoraInicio().toString(), reserva.getHoraFin().toString(),
                        reserva.getEstado().name(), reserva.getMotivo(),
                        reserva.getProfesor() == null ? null : reserva.getProfesor().getId(),
                        reserva.getEspacio() == null ? null : reserva.getEspacio().getId(),
                        Integer.toString(reserva.getNumAsistentes()));
            }
        }
    }

    private List<List<String>> leerFilas() throws IOException {
        List<List<String>> filas = new ArrayList<>();
        if (!Files.exists(ruta)) return filas;
        try (BufferedReader reader = new BufferedReader(new FileReader(ruta.toFile(), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (!linea.isBlank()) filas.add(parsearFila(linea));
            }
        }
        return filas;
    }

    private List<String> parsearFila(String linea) {
        return List.of(linea.split(";", -1));
    }

    private void escribirFila(BufferedWriter writer, String... campos) throws IOException {
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) writer.write(';');
            writer.write(campos[i] == null ? "" : campos[i]);
        }
        writer.newLine();
    }

    private IOException errorFormato(int linea, String detalle) {
        return new IOException("Formato CSV incorrecto en " + ruta + ", fila " + linea + ": " + detalle);
    }
}
