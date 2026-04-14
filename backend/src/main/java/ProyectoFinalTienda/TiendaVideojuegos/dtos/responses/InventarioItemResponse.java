package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;

import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Plataformas;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioItemResponse {

    private int inventarioId;
    private VideojuegoResponse videojuego;
    private Plataformas plataforma;
    private double precioDiario;
    private int stockTotal;
    private int stockDisponible;
//    private int stockAlquilado;
//    private int stockDescartado;

}
