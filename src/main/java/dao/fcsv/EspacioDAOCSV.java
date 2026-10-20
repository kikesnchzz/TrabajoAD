package dao.fcsv;

import dao.EspacioDAO;
import modelo.Espacio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class EspacioDAOCSV implements EspacioDAO {
    private final Path ruta;

    public EspacioDAOCSV() {
        this(Path.of("csv", "espacios.csv"));
    }

    public EspacioDAOCSV(Path ruta) {
        this.ruta = ruta;
    }

    @Override
    public ArrayList<Espacio> leerFichero() throws IOException {
        ArrayList<Espacio> espacios = new ArrayList<>();
        List<List<String>> filas = leerFilas();
        for (int i = 0; i < filas.size(); i++) {
            List<String> fila = filas.get(i);
            if (fila.size() != 7) {
                throw errorFormato(i + 1, "se esperaban 7 campos y se encontraron " + fila.size());
            }
            try {
                int capacidad = Integer.parseInt(fila.get(4));
                boolean disponible;
                if ("DISPONIBLE".equalsIgnoreCase(fila.get(6))) {
                    disponible = true;
                } else if ("NO_DISPONIBLE".equalsIgnoreCase(fila.get(6))) {
                    disponible = false;
                } else {
                    throw new IllegalArgumentException("estado de espacio no válido: " + fila.get(6));
                }
                espacios.add(new Espacio(fila.get(0), fila.get(1), fila.get(2), fila.get(3),
                        capacidad, fila.get(5), disponible));
            } catch (IllegalArgumentException e) {
                throw errorFormato(i + 1, e.getMessage());
            }
        }
        return espacios;
    }

    @Override
    public void escribirFichero(ArrayList<Espacio> espacios) throws IOException {
        if (ruta.getParent() != null) Files.createDirectories(ruta.getParent());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ruta.toFile(), StandardCharsets.UTF_8))) {
            for (Espacio espacio : espacios) {
                escribirFila(writer, espacio.getId(), espacio.getNombre(), espacio.getTipo(),
                        espacio.getUbicacion(), Integer.toString(espacio.getCapacidad()), espacio.getEquipamiento(),
                        espacio.isDisponible() ? "DISPONIBLE" : "NO_DISPONIBLE");
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
