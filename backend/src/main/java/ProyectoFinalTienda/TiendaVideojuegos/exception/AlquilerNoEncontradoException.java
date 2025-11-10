package ProyectoFinalTienda.TiendaVideojuegos.exception;

import java.util.NoSuchElementException;

public class AlquilerNoEncontradoException extends NoSuchElementException {
    public AlquilerNoEncontradoException(String message) {
        super(message);
    }
}
