package dao.fdat;

import dao.ProfesorDAO;
import modelo.Profesor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class ProfesorDAODAT implements ProfesorDAO {
    private final Path ruta;

    public ProfesorDAODAT() {
        this(Path.of("dat", "profesores.dat"));
    }

    public ProfesorDAODAT(Path ruta) {
        this.ruta = ruta;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ArrayList<Profesor> leerFichero() throws IOException {

        if (!Files.exists(ruta) || Files.size(ruta) == 0) return new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(ruta)))) {
            return (ArrayList<Profesor>) in.readObject();
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new IOException("Formato binario incorrecto en " + ruta + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void escribirFichero(ArrayList<Profesor> lista) throws IOException {
        if (ruta.getParent() != null) Files.createDirectories(ruta.getParent());
        try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(ruta)))) {
            out.writeObject(lista);
        }
    }
}
