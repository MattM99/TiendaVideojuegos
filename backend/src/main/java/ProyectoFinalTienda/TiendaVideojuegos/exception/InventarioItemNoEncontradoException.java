package ProyectoFinalTienda.TiendaVideojuegos.exception;

import java.util.NoSuchElementException;

public class InventarioItemNoEncontradoException extends NoSuchElementException {
    public InventarioItemNoEncontradoException(String message) {
        super(message);
    }
}
