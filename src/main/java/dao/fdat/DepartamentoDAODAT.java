package dao.fdat;

import dao.DepartamentoDAO;
import modelo.Departamento;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class DepartamentoDAODAT implements DepartamentoDAO {
    private final Path ruta;

    public DepartamentoDAODAT() {
        this(Path.of("dat", "departamento.dat"));
    }

    public DepartamentoDAODAT(Path ruta) {
        this.ruta = ruta;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ArrayList<Departamento> leerFichero() throws IOException {

        if (!Files.exists(ruta) || Files.size(ruta) == 0) return new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(ruta)))) {
            return (ArrayList<Departamento>) in.readObject();
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new IOException("Formato binario incorrecto en " + ruta + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void escribirFichero(ArrayList<Departamento> lista) throws IOException {
        if (ruta.getParent() != null) Files.createDirectories(ruta.getParent());
        try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(ruta)))) {
            out.writeObject(lista);
        }
    }
}
