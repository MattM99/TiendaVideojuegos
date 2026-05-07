package ProyectoFinalTienda.TiendaVideojuegos.mappers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.PagoResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PagoEntity;
import org.springframework.stereotype.Component;

@Component
public class PagoMapper {

    public PagoResponse toResponse(PagoEntity entity) {

        if (entity == null) {
            return null;
        }

        return PagoResponse.builder()
                .metodoPago(entity.getMetodoPago())
                .descuento(entity.getDescuento())
                .penalizacionTotal(entity.getPenalizacionTotal())
                .montoFinal(entity.getMontoFinal())
                .build();
    }
}
