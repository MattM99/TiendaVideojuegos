package ProyectoFinalTienda.TiendaVideojuegos.exception;

import java.util.NoSuchElementException;

public class InventarioNoEncontradoException extends NoSuchElementException {
    public InventarioNoEncontradoException(String message) {
        super(message);
    }
}
