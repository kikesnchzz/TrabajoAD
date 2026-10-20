package dao.fdat;

import dao.EspacioDAO;
import modelo.Espacio;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class EspacioDAODAT implements EspacioDAO {
    private final Path ruta;

    public EspacioDAODAT() {
        this(Path.of("dat", "espacios.dat"));
    }

    public EspacioDAODAT(Path ruta) {
        this.ruta = ruta;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ArrayList<Espacio> leerFichero() throws IOException {

        if (!Files.exists(ruta) || Files.size(ruta) == 0) return new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(ruta)))) {
            return (ArrayList<Espacio>) in.readObject();
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new IOException("Formato binario incorrecto en " + ruta + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void escribirFichero(ArrayList<Espacio> lista) throws IOException {
        if (ruta.getParent() != null) Files.createDirectories(ruta.getParent());
        try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(ruta)))) {
            out.writeObject(lista);
        }
    }
}
