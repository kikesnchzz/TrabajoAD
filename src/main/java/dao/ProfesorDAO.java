package dao;

import modelo.Profesor;

import java.io.IOException;
import java.util.ArrayList;

public interface ProfesorDAO {
    ArrayList<Profesor> leerFichero() throws IOException;
    void escribirFichero(ArrayList<Profesor> lista) throws IOException;
}
