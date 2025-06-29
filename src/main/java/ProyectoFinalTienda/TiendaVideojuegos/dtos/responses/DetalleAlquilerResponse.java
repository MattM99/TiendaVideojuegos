package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleAlquilerResponse {

    private int detalle_alquiler_id;

    private InventarioResponse inventario;

    private double subtotal;

}
