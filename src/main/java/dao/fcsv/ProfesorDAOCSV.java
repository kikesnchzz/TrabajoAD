package dao.fcsv;

import dao.ProfesorDAO;
import modelo.Departamento;
import modelo.Profesor;

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

public class ProfesorDAOCSV implements ProfesorDAO {
    private final Path ruta;

    public ProfesorDAOCSV() {
        this(Path.of("csv", "profesores.csv"));
    }

    public ProfesorDAOCSV(Path ruta) {
        this.ruta = ruta;
    }

    @Override
    public ArrayList<Profesor> leerFichero() throws IOException {
        ArrayList<Profesor> profesores = new ArrayList<>();
        List<List<String>> filas = leerFilas();
        for (int i = 0; i < filas.size(); i++) {
            List<String> fila = filas.get(i);
            if (fila.size() != 5) {
                throw errorFormato(i + 1, "se esperaban 5 campos y se encontraron " + fila.size());
            }
            Departamento departamento = fila.get(4).isEmpty() ? null : new Departamento(fila.get(4), null);
            profesores.add(new Profesor(fila.get(0), fila.get(1), fila.get(2), fila.get(3), departamento));
        }
        return profesores;
    }

    @Override
    public void escribirFichero(ArrayList<Profesor> profesores) throws IOException {
        if (ruta.getParent() != null) Files.createDirectories(ruta.getParent());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ruta.toFile(), StandardCharsets.UTF_8))) {
            for (Profesor profesor : profesores) {
                String idDepartamento = profesor.getDepartamento() == null ? null : profesor.getDepartamento().getId();
                escribirFila(writer, profesor.getId(), profesor.getNombre(), profesor.getApellidos(),
                        profesor.getEmail(), idDepartamento);
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
