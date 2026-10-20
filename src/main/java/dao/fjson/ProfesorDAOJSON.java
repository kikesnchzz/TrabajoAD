package dao.fjson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import dao.ProfesorDAO;
import modelo.Departamento;
import modelo.Profesor;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProfesorDAOJSON implements ProfesorDAO {
    private final Path ruta;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ProfesorDAOJSON() {
        this(Path.of("json", "profesores.json"));
    }

    public ProfesorDAOJSON(Path ruta) {
        this.ruta = ruta;
    }

    @Override
    public ArrayList<Profesor> leerFichero() throws IOException {
        ArrayList<Profesor> profesores = new ArrayList<>();
        if (!Files.exists(ruta)) return profesores;
        try (Reader reader = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            List<ProfesorJSON> datos = gson.fromJson(reader, new TypeToken<List<ProfesorJSON>>() {}.getType());
            if (datos == null) return profesores;
            for (ProfesorJSON p : datos) {
                Departamento departamento = p.idDepartamento == null ? null : new Departamento(p.idDepartamento, null);
                profesores.add(new Profesor(p.idProfesor, p.nombre, p.apellidos, p.email, departamento));
            }
        } catch (JsonParseException e) {
            throw new IOException("Formato JSON incorrecto en " + ruta + ": " + e.getMessage(), e);
        }
        return profesores;
    }

    @Override
    public void escribirFichero(ArrayList<Profesor> profesores) throws IOException {
        List<ProfesorJSON> datos = new ArrayList<>();
        for (Profesor p : profesores) {
            String idDepartamento = p.getDepartamento() == null ? null : p.getDepartamento().getId();
            datos.add(new ProfesorJSON(p.getId(), p.getNombre(), p.getApellidos(), p.getEmail(), idDepartamento));
        }
        if (ruta.getParent() != null) Files.createDirectories(ruta.getParent());
        try (Writer writer = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8)) {
            gson.toJson(datos, writer);
        }
    }

    private static class ProfesorJSON {
        String idProfesor;
        String nombre;
        String apellidos;
        String email;
        String idDepartamento;

        ProfesorJSON(String idProfesor, String nombre, String apellidos, String email, String idDepartamento) {
            this.idProfesor = idProfesor;
            this.nombre = nombre;
            this.apellidos = apellidos;
            this.email = email;
            this.idDepartamento = idDepartamento;
        }
    }
}
