package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.DetalleAlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioEntity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class DetalleAlquilerCreateOrReplaceRequest {

    private Integer alquiler_id;

    private Integer inventario_id;

    @NotNull(message = "El subtotal es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private Double subtotal;

    public DetalleAlquilerEntity toEntity(AlquilerEntity alquiler, InventarioEntity inventario) {
        return DetalleAlquilerEntity.builder()
                .alquiler(alquiler)
                .inventario(inventario)
                .subtotal(this.subtotal)
                .build();
    }

}
