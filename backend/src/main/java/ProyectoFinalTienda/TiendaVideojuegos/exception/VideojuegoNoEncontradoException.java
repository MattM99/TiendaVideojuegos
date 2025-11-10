package ProyectoFinalTienda.TiendaVideojuegos.exception;

import java.util.NoSuchElementException;

public class VideojuegoNoEncontradoException extends NoSuchElementException {
    public VideojuegoNoEncontradoException(String message) {
        super(message);
    }
}
