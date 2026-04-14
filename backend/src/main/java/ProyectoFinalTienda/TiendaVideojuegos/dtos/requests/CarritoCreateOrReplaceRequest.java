package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoCreateOrReplaceRequest {

    @NotNull(message = "El ID del alquiler es obligatorio")
    private Integer alquiler_id;

    @NotNull(message = "El ID del inventario es obligatorio")
    private Integer inventario_id;

//    public CarritoEntity toEntity(AlquilerEntity alquiler, InventarioEntity inventario) {
//        return CarritoEntity.builder()
//                .alquiler(alquiler)
//                .inventario(inventario)
//                .build();
//    }

}
