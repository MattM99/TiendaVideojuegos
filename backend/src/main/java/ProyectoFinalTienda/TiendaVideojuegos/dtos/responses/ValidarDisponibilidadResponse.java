package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidarDisponibilidadResponse {

    private boolean puedeCrearAlquiler;

    private List<ItemConStockInsuficienteResponse> faltantes;
}
