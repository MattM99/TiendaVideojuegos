package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioItemUpdateRequest {

    private BigDecimal precioDiario;
    private Integer stockTotal;
    private Integer stockDisponible;

}
