package ProyectoFinalTienda.TiendaVideojuegos.events;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioItemEntity;

public class VideojuegoDevueltoEvent {

    private final InventarioItemEntity inventarioItem;

    public VideojuegoDevueltoEvent(InventarioItemEntity inventarioItem) {
        this.inventarioItem = inventarioItem;
    }

    public InventarioItemEntity getInventarioItem() {
        return inventarioItem;
    }
}
