package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonaResponse {

    private int personaId;

    private String nombre;

    private String apellido;

    private String dni;

    private String email;

    private String telefono;
}
