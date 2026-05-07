package ProyectoFinalTienda.TiendaVideojuegos.mappers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.PenalizacionResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PenalizacionEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PenalizacionMapper {

    public PenalizacionResponse toResponse(PenalizacionEntity entity) {

        if (entity == null) {
            return null;
        }

        return PenalizacionResponse.builder()
                .motivo(entity.getMotivo())
                .monto(entity.getMonto())
                .build();
    }

    public List<PenalizacionResponse> toResponseList(
            List<PenalizacionEntity> entities
    ) {

        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toResponse)
                .toList();
    }
}
