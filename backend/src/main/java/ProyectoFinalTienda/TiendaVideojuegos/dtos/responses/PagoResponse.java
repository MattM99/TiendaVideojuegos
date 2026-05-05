package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoPago;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoResponse {

    private int pagoId;
    private AlquilerResponse alquilerResponse;
    private EstadoPago estadoPago;
    private BigDecimal descuento;
    private BigDecimal penalizacionTotal;
    private BigDecimal montoFinal;

}
