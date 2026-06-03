package ProyectoFinalTienda.TiendaVideojuegos.mappers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.ReservaResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.ReservaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReservaMapper {

    public ReservaResponse toResponse(ReservaEntity entity) {

        return ReservaResponse.builder()
                .reservaId(entity.getReservaId())
                .personaId(entity.getPersona().getPersonaId())
                .inventarioItemId(
                        entity.getInventarioItem().getInventarioItemId()
                )
                .estadoReserva(entity.getEstadoReserva())
                .fechaReserva(entity.getFechaReserva())
                .fechaNotificacion(entity.getFechaNotificacion())
                .build();
    }

    public List<ReservaResponse> toResponseList(
            List<ReservaEntity> reservas
    ) {
        return reservas.stream()
                .map(this::toResponse)
                .toList();
    }
}
