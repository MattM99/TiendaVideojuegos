package ProyectoFinalTienda.TiendaVideojuegos.exception;

import java.util.NoSuchElementException;

public class UsuarioNoEncontradoException extends NoSuchElementException {
    public UsuarioNoEncontradoException(String message) {
        super(message);
    }
}
