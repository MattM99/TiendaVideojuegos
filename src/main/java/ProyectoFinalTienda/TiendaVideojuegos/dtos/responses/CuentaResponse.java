package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
public class CuentaResponse {
    private String nickname;
    private String rol;
    private String estado;
}
