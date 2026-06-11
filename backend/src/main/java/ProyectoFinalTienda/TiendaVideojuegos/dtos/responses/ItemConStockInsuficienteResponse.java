package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;

import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Plataformas;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemConStockInsuficienteResponse {

    private Integer inventarioItemId;

    private String titulo;

    private Plataformas plataforma;

    private Integer cantidadSolicitada;

    private Integer cantidadDisponible;
}
