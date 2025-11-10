package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;

import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Plataformas;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioResponse {

    private int inventarioId;
    private VideojuegoResponse videojuego;
    private Plataformas plataforma;
    private double precioUnitarioDiario;
    private int stockTotal;
    private int stockDisponible;
    private int stockAlquilado;
    private int stockDescartado;

}
