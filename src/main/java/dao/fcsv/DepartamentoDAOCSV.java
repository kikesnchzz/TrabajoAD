package dao.fcsv;

import dao.DepartamentoDAO;
import modelo.Departamento;

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

public class DepartamentoDAOCSV implements DepartamentoDAO {
    private final Path ruta;

    public DepartamentoDAOCSV() {
        this(Path.of("csv", "departamento.csv"));
    }

    public DepartamentoDAOCSV(Path ruta) {
        this.ruta = ruta;
    }

    @Override
    public ArrayList<Departamento> leerFichero() throws IOException {
        ArrayList<Departamento> departamentos = new ArrayList<>();
        List<List<String>> filas = leerFilas();
        for (int i = 0; i < filas.size(); i++) {
            List<String> fila = filas.get(i);
            if (fila.size() != 2) {
                throw errorFormato(i + 1, "se esperaban 2 campos y se encontraron " + fila.size());
            }
            departamentos.add(new Departamento(fila.get(0), fila.get(1)));
        }
        return departamentos;
    }

    @Override
    public void escribirFichero(ArrayList<Departamento> departamentos) throws IOException {
        if (ruta.getParent() != null) {
            Files.createDirectories(ruta.getParent());
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ruta.toFile(), StandardCharsets.UTF_8))) {
            for (Departamento departamento : departamentos) {
                escribirFila(writer, departamento.getId(), departamento.getNombre());
            }
        }
    }

    private List<List<String>> leerFilas() throws IOException {
        List<List<String>> filas = new ArrayList<>();
        if (!Files.exists(ruta)) {
            return filas;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(ruta.toFile(), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (!linea.isBlank()) {
                    filas.add(parsearFila(linea));
                }
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
