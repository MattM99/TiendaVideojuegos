package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;

import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Plataformas;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioItemResponse {

    private int inventarioId;
    private VideojuegoResponse videojuego;
    private Plataformas plataforma;
    private BigDecimal precioDiario;
    private int stockTotal;
    private int stockDisponible;
//    private int stockAlquilado;
//    private int stockDescartado;

}
