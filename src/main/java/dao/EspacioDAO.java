package dao;

import modelo.Espacio;

import java.io.IOException;
import java.util.ArrayList;

public interface EspacioDAO {
    ArrayList<Espacio> leerFichero() throws IOException;
    void escribirFichero(ArrayList<Espacio> lista) throws IOException;
}
