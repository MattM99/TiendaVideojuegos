package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
public class CuentaResponse {
    private String nickname;
    private String rol;
    private String estado;
}
