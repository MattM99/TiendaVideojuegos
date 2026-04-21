package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleAlquilerResponse {

    private int detalleAlquilerId;

    private InventarioItemResponse inventario;

    private double subtotal;

}
