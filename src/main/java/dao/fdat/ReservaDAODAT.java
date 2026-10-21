package dao.fdat;

import dao.ReservaDAO;
import modelo.Reserva;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class ReservaDAODAT implements ReservaDAO {
    private final Path ruta;

    public ReservaDAODAT() {
        this(Path.of("dat", "reserva.dat"));
    }

    public ReservaDAODAT(Path ruta) {
        this.ruta = ruta;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ArrayList<Reserva> leerFichero() throws IOException {

        if (!Files.exists(ruta) || Files.size(ruta) == 0) return new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(ruta)))) {
            return (ArrayList<Reserva>) in.readObject();
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new IOException("Formato binario incorrecto en " + ruta + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void escribirFichero(ArrayList<Reserva> lista) throws IOException {
        if (ruta.getParent() != null) Files.createDirectories(ruta.getParent());
        try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(ruta)))) {
            out.writeObject(lista);
        }
    }
}
