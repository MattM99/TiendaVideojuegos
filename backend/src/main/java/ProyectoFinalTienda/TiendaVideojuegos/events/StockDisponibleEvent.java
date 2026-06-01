package ProyectoFinalTienda.TiendaVideojuegos.events;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioItemEntity;

public class StockDisponibleEvent {

    private final InventarioItemEntity inventarioItemEntity;

    public StockDisponibleEvent(InventarioItemEntity inventarioItemEntity) {
        this.inventarioItemEntity = inventarioItemEntity;
    }

    public InventarioItemEntity getInventarioItem() {
        return inventarioItemEntity;
    }
}
