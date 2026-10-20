package dao.fjson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import dao.DepartamentoDAO;
import modelo.Departamento;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DepartamentoDAOJSON implements DepartamentoDAO {
    private final Path ruta;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public DepartamentoDAOJSON() {
        this(Path.of("json", "departamento.json"));
    }

    public DepartamentoDAOJSON(Path ruta) {
        this.ruta = ruta;
    }

    @Override
    public ArrayList<Departamento> leerFichero() throws IOException {
        ArrayList<Departamento> departamentos = new ArrayList<>();
        if (!Files.exists(ruta)) return departamentos;
        try (Reader reader = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            List<DepartamentoJSON> datos = gson.fromJson(reader, new TypeToken<List<DepartamentoJSON>>() {}.getType());
            if (datos == null) return departamentos;
            for (DepartamentoJSON d : datos) {
                departamentos.add(new Departamento(d.idDepartamento, d.nombre));
            }
        } catch (JsonParseException e) {
            throw new IOException("Formato JSON incorrecto en " + ruta + ": " + e.getMessage(), e);
        }
        return departamentos;
    }

    @Override
    public void escribirFichero(ArrayList<Departamento> departamentos) throws IOException {
        List<DepartamentoJSON> datos = new ArrayList<>();
        for (Departamento d : departamentos) {
            datos.add(new DepartamentoJSON(d.getId(), d.getNombre()));
        }
        if (ruta.getParent() != null) Files.createDirectories(ruta.getParent());
        try (Writer writer = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8)) {
            gson.toJson(datos, writer);
        }
    }

    private static class DepartamentoJSON {
        String idDepartamento;
        String nombre;

        DepartamentoJSON(String idDepartamento, String nombre) {
            this.idDepartamento = idDepartamento;
            this.nombre = nombre;
        }
    }
}
