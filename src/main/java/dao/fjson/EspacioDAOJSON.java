package dao.fjson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import dao.EspacioDAO;
import modelo.Espacio;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class EspacioDAOJSON implements EspacioDAO {
    private static final String DISPONIBLE = "DISPONIBLE";
    private static final String NO_DISPONIBLE = "NO_DISPONIBLE";

    private final Path ruta;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public EspacioDAOJSON() {
        this(Path.of("json", "espacios.json"));
    }

    public EspacioDAOJSON(Path ruta) {
        this.ruta = ruta;
    }

    @Override
    public ArrayList<Espacio> leerFichero() throws IOException {
        ArrayList<Espacio> espacios = new ArrayList<>();
        if (!Files.exists(ruta)) return espacios;
        try (Reader reader = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            List<EspacioJSON> datos = gson.fromJson(reader, new TypeToken<List<EspacioJSON>>() {}.getType());
            if (datos == null) return espacios;
            for (EspacioJSON e : datos) {
                boolean disponible = DISPONIBLE.equalsIgnoreCase(e.estado);
                espacios.add(new Espacio(e.idEspacio, e.nombre, e.tipo, e.ubicacion, e.capacidad, e.equipamiento, disponible));
            }
        } catch (JsonParseException e) {
            throw new IOException("Formato JSON incorrecto en " + ruta + ": " + e.getMessage(), e);
        }
        return espacios;
    }

    @Override
    public void escribirFichero(ArrayList<Espacio> espacios) throws IOException {
        List<EspacioJSON> datos = new ArrayList<>();
        for (Espacio e : espacios) {
            datos.add(new EspacioJSON(e.getId(), e.getNombre(), e.getTipo(), e.getUbicacion(), e.getCapacidad(),
                    e.getEquipamiento(), e.isDisponible() ? DISPONIBLE : NO_DISPONIBLE));
        }
        if (ruta.getParent() != null) Files.createDirectories(ruta.getParent());
        try (Writer writer = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8)) {
            gson.toJson(datos, writer);
        }
    }

    private static class EspacioJSON {
        String idEspacio;
        String nombre;
        String tipo;
        String ubicacion;
        int capacidad;
        String equipamiento;
        String estado;

        EspacioJSON(String idEspacio, String nombre, String tipo, String ubicacion, int capacidad, String equipamiento, String estado) {
            this.idEspacio = idEspacio;
            this.nombre = nombre;
            this.tipo = tipo;
            this.ubicacion = ubicacion;
            this.capacidad = capacidad;
            this.equipamiento = equipamiento;
            this.estado = estado;
        }
    }
}
