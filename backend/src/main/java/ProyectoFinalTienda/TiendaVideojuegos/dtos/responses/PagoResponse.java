package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;

import ProyectoFinalTienda.TiendaVideojuegos.model.enums.MetodoPago;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoResponse {

    private MetodoPago metodoPago;

    private BigDecimal descuento;

    private BigDecimal penalizacionTotal;

    private BigDecimal montoFinal;

}
