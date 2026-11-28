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

    private List<DisponibilidadItem> disponibilidad;

    public record DisponibilidadItem(
            int inventarioItemId,
            int cantidadSolicitada,
            int stockLibre,
            int cantidadDisponibleParaCliente,
            boolean reservaPropiaVigente,
            String motivo
    ) {}
}
