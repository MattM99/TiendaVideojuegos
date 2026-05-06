package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleAlquilerResponse {

    private int detalleAlquilerId;

    private InventarioItemResponse inventario;

    private BigDecimal subtotal;

}
