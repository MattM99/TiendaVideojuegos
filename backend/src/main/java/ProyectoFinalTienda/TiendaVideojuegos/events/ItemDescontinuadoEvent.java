package ProyectoFinalTienda.TiendaVideojuegos.events;

public class ItemDescontinuadoEvent {

    private final int inventarioItemId;

    public ItemDescontinuadoEvent(int inventarioItemId) {
        this.inventarioItemId = inventarioItemId;
    }

    public int getInventarioItemId() {
        return inventarioItemId;
    }

}
