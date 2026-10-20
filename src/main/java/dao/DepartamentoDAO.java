package dao;

import modelo.Departamento;

import java.io.IOException;
import java.util.ArrayList;

public interface DepartamentoDAO {
    ArrayList<Departamento> leerFichero() throws IOException;
    void escribirFichero(ArrayList<Departamento> lista) throws IOException;
}
