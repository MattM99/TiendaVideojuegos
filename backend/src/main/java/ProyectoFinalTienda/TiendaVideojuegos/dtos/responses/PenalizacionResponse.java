package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PenalizacionResponse {

    private String motivo;

    private BigDecimal monto;

}
