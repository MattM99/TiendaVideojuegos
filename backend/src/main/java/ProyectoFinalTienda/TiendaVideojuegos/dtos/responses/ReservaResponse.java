package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;

import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoReserva;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaResponse {

    private Integer reservaId;
    private Integer personaId;
    private Integer inventarioItemId;
    private EstadoReserva estadoReserva;
    private LocalDateTime fechaReserva;
    private LocalDateTime fechaNotificacion;
}
