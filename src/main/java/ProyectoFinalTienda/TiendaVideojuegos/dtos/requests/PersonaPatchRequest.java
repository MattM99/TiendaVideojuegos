package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonaPatchRequest {
    private String nombre;
    private String apellido;
    private String telefono;
    private String dni;
}
