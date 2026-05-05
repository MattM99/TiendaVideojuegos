package ProyectoFinalTienda.TiendaVideojuegos.mappers;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.PagoResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PagoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PagoMapper {
    @Autowired
    AlquilerMapper alquilerMapper;

    public PagoResponse toResponse(PagoEntity pago) {
        return PagoResponse.builder()
                .pagoId(pago.getPagoId())
                .alquilerResponse(alquilerMapper.toResponse(pago.getAlquiler()))
                .estadoPago(pago.getEstadoPago())
                .descuento(pago.getDescuento())
                .penalizacionTotal(pago.getPenalizacionTotal())
                .montoFinal(pago.getMontoFinal())
                .build();
    }

}
