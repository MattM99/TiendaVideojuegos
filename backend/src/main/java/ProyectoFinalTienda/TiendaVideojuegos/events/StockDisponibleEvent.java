package ProyectoFinalTienda.TiendaVideojuegos.events;

public class StockDisponibleEvent {

    private final int inventarioItemId;

    public StockDisponibleEvent(int inventarioItemId) {
        this.inventarioItemId = inventarioItemId;
    }

    public int getInventarioItemId() {
        return inventarioItemId;
    }

}
