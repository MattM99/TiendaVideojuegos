package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PenalizacionResponse {

    private String motivo;

    private BigDecimal monto;

}
