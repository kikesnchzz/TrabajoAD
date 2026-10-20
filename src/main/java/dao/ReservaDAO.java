package dao;

import modelo.Reserva;

import java.io.IOException;
import java.util.ArrayList;

public interface ReservaDAO {
    ArrayList<Reserva> leerFichero() throws IOException;
    void escribirFichero(ArrayList<Reserva> lista) throws IOException;
}
