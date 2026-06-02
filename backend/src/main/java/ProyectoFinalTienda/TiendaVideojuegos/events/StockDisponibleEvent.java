package ProyectoFinalTienda.TiendaVideojuegos.events;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioItemEntity;

public class StockDisponibleEvent {

    private final int inventarioItemId;

    public StockDisponibleEvent(int inventarioItemId) {
        this.inventarioItemId = inventarioItemId;
    }

    public int getInventarioItemId() {
        return inventarioItemId;
    }
}
