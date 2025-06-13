package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.DetalleAlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioEntity;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleAlquilerCreateOrReplaceRequest {

    @NotNull(message = "El ID del alquiler es obligatorio")
    private Integer alquiler_id;

    @NotNull(message = "El ID del inventario es obligatorio")
    private Integer inventario_id;

//    public DetalleAlquilerEntity toEntity(AlquilerEntity alquiler, InventarioEntity inventario) {
//        return DetalleAlquilerEntity.builder()
//                .alquiler(alquiler)
//                .inventario(inventario)
//                .build();
//    }

}
