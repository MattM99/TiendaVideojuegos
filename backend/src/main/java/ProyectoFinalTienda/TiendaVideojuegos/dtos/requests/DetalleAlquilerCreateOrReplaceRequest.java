package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleAlquilerCreateOrReplaceRequest {

    @NotNull(message = "El ID del alquiler es obligatorio")
    private Integer alquilerId;

    @NotNull(message = "El ID del inventario es obligatorio")
    private Integer inventarioItemId;

//    public DetalleAlquilerEntity toEntity(AlquilerEntity alquiler, InventarioItemEntity inventario) {
//        return DetalleAlquilerEntity.builder()
//                .alquiler(alquiler)
//                .inventario(inventario)
//                .build();
//    }

}
